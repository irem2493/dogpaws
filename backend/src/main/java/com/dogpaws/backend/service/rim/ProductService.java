package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.common.FileDto;
import com.dogpaws.backend.dto.rim.ProductDto;
import com.dogpaws.backend.dto.rim.ProductListDto;
import com.dogpaws.backend.dto.rim.ProductOptionDto;
import com.dogpaws.backend.dto.rim.ProductSearchDto;
import com.dogpaws.backend.entity.rim.Product;
import com.dogpaws.backend.entity.rim.ProductInbound;
import com.dogpaws.backend.entity.rim.ProductOption;
import com.dogpaws.backend.repository.dao.rim.OrderDao;
import com.dogpaws.backend.repository.dao.rim.ProductDao;
import com.dogpaws.backend.repository.jpa.rim.ProductInboundRepository;
import com.dogpaws.backend.repository.jpa.rim.ProductOptionRepository;
import com.dogpaws.backend.repository.jpa.rim.ProductRepository;
import com.dogpaws.backend.utils.FileUploadUtil;
import com.dogpaws.backend.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductInboundRepository productInboundRepository;
    private final OrderDao orderDao;
    private final ProductDao productDao;

    private final FileUploadUtil fileUploadUtil;

    /**
     * 상품 등록 (JPA)
     */
    public void registProduct(ProductDto productDto, List<ProductOptionDto> optionDtos, MultipartFile thumbnailImage, MultipartFile detailImage, String userId) throws IOException {
        log.info("상품 등록 시작. 상품: {}, 사용자: {}", productDto.toString(), userId);

        // 총 재고 수량 계산 TODO : 메서드로 분리
        int baseStock = productDto.getStockQuantity();
        int totalStock = 0;

        Product product = Product.builder()
                //notnull
                .name(productDto.getName())
                .price(productDto.getPrice())
                .stockQuantity(productDto.getStockQuantity())
                .description(productDto.getDescription())
                .status(productDto.getStatus())
                .mainCategory(productDto.getMainCategory())
                .manufacturer(productDto.getManufacturer())
                //nullable
                .subCategory(productDto.getSubCategory())
                .material(productDto.getMaterial())
                .origin(productDto.getOrigin())
                .expirationDate(StringUtil.stringToLocalDate(productDto.getExpirationDate()))
                .weight(productDto.getWeight())
                .build();

        log.debug("상품 detail: {}", product);
        Product finalProduct = productRepository.save(product);
        log.info("생성된 product ID: {}", finalProduct.getProductId());

        // 기본 옵션 생성 및 저장
        ProductOption baseOption = ProductOption.builder()
                .product(finalProduct)
                .optionName(finalProduct.getName() + " (기본)")
                .optionPrice(finalProduct.getPrice())
                .optionStock(productDto.getBasicOptionQuantity())
                .isBaseOption(true)
                .build();
        productOptionRepository.save(baseOption);

        // 기본 옵션 입고 처리
        ProductInbound baseInbound = ProductInbound.builder()
                .optionId(baseOption.getOptionId().longValue())
                .quantity(productDto.getBasicOptionQuantity())
                .costPrice(productDto.getCostPrice())
                .build();
        productInboundRepository.save(baseInbound);

        if(optionDtos != null && !optionDtos.isEmpty()) {
            log.info("  {} 개 상품 옵션 생성중 ", optionDtos.size());
            for (ProductOptionDto optionDto : optionDtos) {
                ProductOption option = ProductOption.builder()
                        .product(finalProduct)
                        .optionName(optionDto.getOptionName())
                        .optionPrice(optionDto.getOptionPrice())
                        .optionStock(optionDto.getOptionStock())
                        // 추가된 옵션 필드들
                        .optionSize(optionDto.getOptionSize())
                        .optionColor(optionDto.getOptionColor())
                        .optionWeight(optionDto.getOptionWeight())
                        .optionMaterial(optionDto.getOptionMaterial())
                        .optionExpirationDate(StringUtil.stringToLocalDate(optionDto.getOptionExpirationDate()))
                        .optionStorageInfo(optionDto.getOptionStorageInfo())
                        .optionManufacturer(optionDto.getOptionManufacturer())
                        .optionOrigin(optionDto.getOptionOrigin())
                        .build();

                        ProductOption savedOption = productOptionRepository.save(option);
                        log.debug("상품 옵션 생성됨: {}", option);

                        // 각 옵션별 입고 처리
                        ProductInbound optionInbound = ProductInbound.builder()
                                .optionId(savedOption.getOptionId().longValue())
                                .quantity(optionDto.getOptionStock())
                                .costPrice(optionDto.getCostPrice())
                                .build();
                        productInboundRepository.save(optionInbound);
            }
        }

        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            log.info("상품썸네일 이미지 Id: {}", product.getProductId());
            FileDto thumbnailFile = fileUploadUtil.saveFile(
                    thumbnailImage,
                    "PM",
                    product.getProductId().toString(),
                    userId
            );
            product.updateImages(thumbnailFile.getFileUrl(), null);
            log.debug("생성된 상품 썸네일 URL: {}", thumbnailFile.getFileUrl());
        }

        if (detailImage != null && !detailImage.isEmpty()) {
            log.info("상품 상세 이미지 Id: {}", product.getProductId());
            FileDto detailFile = fileUploadUtil.saveFile(
                    detailImage,
                    "PD",
                    product.getProductId().toString(),
                    userId
            );
            product.updateImages(
                    product.getImageUrl(),
                    detailFile.getFileUrl()
            );
            log.debug("생성된 상품 상세 이미지 URL: {}", detailFile.getFileUrl());
        }
        productRepository.save(finalProduct);
    }

    /**
     * 제품 상세 조회 (JPA)
     */
    public ProductDto getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return convertProductToProductDto(product);
    }

    public Page<ProductListDto> searchProducts(ProductSearchDto searchDto) {
        // offset 계산
        searchDto.setOffset((searchDto.getPage() - 1) * searchDto.getPageSize());

        try {
            // sortBy 파라미터 검증
            if (!isValidSortBy(searchDto.getSortBy())) {
                searchDto.setSortBy(null); // 잘못된 값이면 기본 정렬 사용
            }

            // 데이터 조회
            List<ProductListDto> content = productDao.searchProducts(searchDto);
            int total = productDao.getTotalCount(searchDto);

            return new PageImpl<>(content,
                    PageRequest.of(searchDto.getPage() - 1, searchDto.getPageSize()),
                    total);

        } catch (Exception e) {
            log.error("상품 검색 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("상품 검색 실패", e);
        }
    }

    private boolean isValidSortBy(String sortBy) {
        return sortBy == null || sortBy.matches("^(stock_asc|stock_desc|price_asc|price_desc)$");
    }

    /**
     * 상품 상태 변경 (JPA)
     */
    @Transactional
    public void updateProductStatus(Long productId, String status) {
        // 상태값 검증
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("잘못된 상품 상태값입니다: " + status);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 재고가 0인 경우 품절 상태로만 변경 가능
        if (product.getStockQuantity() == 0 && !status.equals(ProductDto.Status.SOLD_OUT.getCode())) {
            throw new IllegalStateException("재고가 없는 상품은 품절 상태로만 변경할 수 있습니다.");
        }

        product.update(
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                status,
                product.getMainCategory(),
                product.getSubCategory(),
                product.getMaterial(),
                product.getOrigin(),
                product.getExpirationDate(),
                product.getWeight()
        );

        log.info("상품 상태 변경 완료. productId: {}, status: {}", productId, status);
    }

    /**
     * 상품 삭제 (JPA + MyBatis)
     */
    @Transactional
    public void deleteProduct(Long productId) {
        // 주문 진행중인 상품인지 확인 (MyBatis)
        if (productDao.hasActiveOrders(productId)) {
            throw new IllegalStateException("해당 상품에 대한 주문이 진행중입니다.");
        }

        productRepository.deleteById(productId);
        log.info("상품 삭제 완료. productId: {}", productId);
    }


    /**
     * 옵션 추가 (JPA)
     */
    public void addProductOption(Long productId, ProductOptionDto productOptionDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));

        boolean isDuplicate = product.getOptions().stream()
                .anyMatch(option -> option.getOptionName().equals(productOptionDto.getOptionName()));
        if(isDuplicate){
            throw new IllegalArgumentException("이미 존재하는 옵션명 입니다.");
        }

        ProductOption productOption = ProductOption.builder()
                .product(product)
                .optionName(productOptionDto.getOptionName())
                .optionPrice(productOptionDto.getOptionPrice())
                .optionStock(productOptionDto.getOptionStock())
                // 추가 필드
                .optionSize(productOptionDto.getOptionSize())
                .optionColor(productOptionDto.getOptionColor())
                .optionWeight(productOptionDto.getOptionWeight())
                .optionMaterial(productOptionDto.getOptionMaterial())
                .optionExpirationDate(StringUtil.stringToLocalDate(productOptionDto.getOptionExpirationDate()))
                .optionStorageInfo(productOptionDto.getOptionStorageInfo())
                .optionManufacturer(productOptionDto.getOptionManufacturer())
                .optionOrigin(productOptionDto.getOptionOrigin())
                .build();

        log.info("product id : {}, 옵션 추가 성공 , 옵션 이름 : {}", productId, productOption.getOptionName());
        productOptionRepository.save(productOption);
    }

    /**
     * 해당 상품 옵션목록 조회 (JPA)
     */
    public List<ProductOptionDto> getProductOptions(Long productId) {
        List<ProductOption> options = productOptionRepository.findByProductId(productId);
        return options.stream()
                .map(option -> {
                    ProductOptionDto dto = new ProductOptionDto();
                    dto.setOptionId(option.getOptionId());
                    dto.setOptionName(option.getOptionName());
                    dto.setOptionPrice(option.getOptionPrice());
                    dto.setProductId(productId);
                    dto.setBaseOption(option.isBaseOption());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 옵션의 진행중인 주문 여부 확인
     */
    public boolean checkActiveOrders(Long productId, Long optionId) {
        return orderDao.hasActiveOrders(productId, optionId);
    }

    /**
     * 옵션 수정 (MyBatis)
     */
    public void updateProductOption(ProductOptionDto optionDto) {
        // 진행중인 주문 확인
        boolean hasActiveOrders = orderDao.hasActiveOrders(optionDto.getProductId() ,optionDto.getOptionId().longValue());

        if (hasActiveOrders) {
            // 진행중인 주문이 있으면 옵션명만 수정 가능
            productDao.updateOptionName(optionDto.getOptionId().longValue(), optionDto.getOptionName());
        } else {
            // 진행중인 주문이 없으면 모든 정보 수정 가능
            productDao.updateOption(optionDto);
        }
    }

    /**
     * 옵션 soft 삭제 (MyBatis)
     */
    public void deleteProductOption(Long productId, Long optionId) {
        // 진행중인 주문 확인
        boolean hasActiveOrders = orderDao.hasActiveOrders(productId, optionId);

        if (hasActiveOrders) {
            // 진행중인 주문이 있으면 상태만 '판매중지'로 변경
            productDao.updateOptionStatus(optionId, "D");
        } else {
            // 진행중인 주문이 없으면 실제 삭제 가능
            productDao.deleteOption(optionId);
        }
    }

    /**
     * 각 카테고리별 베스트 상품 조회
     */
    public Map<String, List<ProductListDto>> getAllBestProducts(int size) {
        Map<String, List<ProductListDto>> result = new HashMap<>();

        result.put("bestFoods", productDao.getBestProducts("F", size));
        result.put("bestSnacks", productDao.getBestProducts("N", size));
        result.put("bestToys", productDao.getBestProducts("T", size));

        return result;
    }


    /**
     * 재고 관리
     */

    /**
     * 재고 수량 변경 처리 - 옵션별 (JPA)
     */
    @Transactional
    public void updateStock(Long productId, Long optionId, Integer quantity, boolean isIncrease) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                processStockUpdate(productId, optionId, quantity, isIncrease);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    throw new RuntimeException("재고 처리 실패. 잠시 후 다시 시도해주세요.");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재고 처리가 중단되었습니다.");
                }
            }
        }
    }

    private void processStockUpdate(Long productId, Long optionId, Integer quantity, boolean isIncrease) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }

        // MyBatis로 비관적 락 조회
        Product product = productDao.findByIdWithLock(productId);
        if (product == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }

        ProductOption option = productDao.findOptionByIdWithLock(optionId);
        if (option == null) {
            throw new IllegalArgumentException("옵션을 찾을 수 없습니다.");
        }

        // 옵션이 해당 상품의 것인지 확인
        if (!option.getProduct().getProductId().equals(productId)) {
            throw new IllegalArgumentException("해당 상품의 옵션이 아닙니다.");
        }

        // 재고 감소 시 재고 체크
        if (!isIncrease && option.getOptionStock() < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        // 옵션 재고 변경
        int newOptionStock = isIncrease ?
                option.getOptionStock() + quantity :
                option.getOptionStock() - quantity;
        option.updateStock(newOptionStock);

        // MyBatis로 전체 재고 합계 조회
        int totalStock = productDao.getTotalStockByProductId(productId);

        // 상품 상태 결정
        String newStatus = product.getStatus();
        if (totalStock == 0) {
            newStatus = ProductDto.Status.SOLD_OUT.getCode();
        } else if (product.getStatus().equals(ProductDto.Status.SOLD_OUT.getCode())) {
            newStatus = ProductDto.Status.ON_SALE.getCode();
        }

        // JPA로 엔티티 업데이트
        product.update(
                product.getName(),
                product.getPrice(),
                totalStock,
                product.getDescription(),
                newStatus,
                product.getMainCategory(),
                product.getSubCategory(),
                product.getMaterial(),
                product.getOrigin(),
                product.getExpirationDate(),
                product.getWeight()
        );

        // 변경사항 저장
        productRepository.save(product);
        productOptionRepository.save(option);

        log.info("재고 {} 처리 완료. productId: {}, optionId: {}, 수량: {}, 총재고: {}",
                isIncrease ? "입고" : "출고", productId, optionId, quantity, totalStock);
    }
    /**
     * 현재 상품 옵션들 재고 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Integer> getCurrentStock(Long productId, Long optionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        // 전체 옵션의 재고 합계 계산
        int totalStock = productDao.getTotalStockByProductId(productId);

        Map<String, Integer> stockInfo = new HashMap<>();
        stockInfo.put("optionStock", option.getOptionStock());
        stockInfo.put("totalStock", totalStock);

        return stockInfo;
    }

    /**
     * 상품의 모든 재고 정보 조회 (옵션정보포함)
     */
    public Map<String, Object> getAllStockInfo(Long productId) {
        Map<String, Object> result = new HashMap<>();

        // 상품 기본 정보 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 옵션 목록 조회
        List<ProductOption> options = productDao.findByProductProductId(productId);

        // 전체 재고 계산
        int totalStock = productDao.getTotalStockByProductId(productId);

        // 옵션별 재고 정보 구성
        List<Map<String, Object>> optionStocks = options.stream()
                .map(option -> {
                    Map<String, Object> optionInfo = new HashMap<>();
                    optionInfo.put("optionId", option.getOptionId());
                    optionInfo.put("optionName", option.getOptionName());
                    optionInfo.put("optionStock", option.getOptionStock());
                    return optionInfo;
                })
                .collect(Collectors.toList());

        result.put("productId", productId);
        result.put("productName", product.getName());
        result.put("totalStock", totalStock);
        result.put("options", optionStocks);

        return result;
    }


    /** util ...
     * Entity -> Dto 변환
     */
    private ProductDto convertProductToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
            productDto.setProductId(product.getProductId());
            productDto.setName(product.getName());
            productDto.setPrice(product.getPrice());
            productDto.setStockQuantity(product.getStockQuantity());
            productDto.setDescription(product.getDescription());
            productDto.setImageUrl(product.getImageUrl());
            productDto.setImageDetailUrl(product.getImageDetailUrl());
            productDto.setStatus(product.getStatus());
            productDto.setSize(product.getSize());
            productDto.setMaterial(product.getMaterial());
            productDto.setManufacturer(product.getManufacturer());
            productDto.setOrigin(product.getOrigin());
            if(product.getExpirationDate() != null) {
                productDto.setExpirationDate(product.getExpirationDate().toString());
            }
            productDto.setColor(product.getColor());
            productDto.setCreatedAt(product.getCreatedAt());
            productDto.setUpdatedAt(product.getUpdatedAt());
            productDto.setMainCategory(product.getMainCategory());
            productDto.setSubCategory(product.getSubCategory());
            productDto.setStorageInfo(product.getStorageInfo());
            productDto.setWeight(product.getWeight());

        // 옵션 정보 설정
        List<ProductOptionDto> optionDtos = product.getOptions().stream()
                .map(this::convertToOptionDto)
                .collect(Collectors.toList());

        productDto.setOptions(optionDtos);
        return productDto;
    }

    private ProductOptionDto convertToOptionDto(ProductOption productOption) {
        ProductOptionDto productOptionDto = new ProductOptionDto();
        // 기존 필드
        productOptionDto.setOptionId(productOption.getOptionId());
        productOptionDto.setOptionName(productOption.getOptionName());
        productOptionDto.setOptionPrice(productOption.getOptionPrice());
        productOptionDto.setOptionStock(productOption.getOptionStock());
        productOptionDto.setBaseOption(productOption.isBaseOption());

        // 추가 필드
        productOptionDto.setOptionSize(productOption.getOptionSize());
        productOptionDto.setOptionColor(productOption.getOptionColor());
        productOptionDto.setOptionWeight(productOption.getOptionWeight());
        productOptionDto.setOptionMaterial(productOption.getOptionMaterial());
        productOptionDto.setOptionExpirationDate(
                productOption.getOptionExpirationDate() != null ?
                        productOption.getOptionExpirationDate().toString() : null
        );
        productOptionDto.setOptionStorageInfo(productOption.getOptionStorageInfo());
        productOptionDto.setOptionManufacturer(productOption.getOptionManufacturer());
        productOptionDto.setOptionOrigin(productOption.getOptionOrigin());

        return productOptionDto;
    }

    /**
     * 상태 값 검증 메서드
     */
    private boolean isValidStatus(String status) {
        return status != null && (
                status.equals(ProductDto.Status.ON_SALE.getCode()) ||
                        status.equals(ProductDto.Status.SOLD_OUT.getCode()) ||
                        status.equals(ProductDto.Status.DISCONTINUED.getCode())
        );
    }

    /**
     * 모든 상품 검색
     */
    public List<ProductListDto> searchAllProducts(ProductSearchDto searchDto) {
        try {
            // sortBy 파라미터 검증
            if (searchDto.getSortBy() != null) {
                if (!searchDto.getSortBy().matches("^(stock_asc|stock_desc)$")) {
                    searchDto.setSortBy(null); // 잘못된 값이면 기본 정렬 사용
                }
            }

            log.info("ProductSearchDto 검색파라미터 {}", searchDto.getSearchKeyword());

            // 페이징 관련 파라미터 제거
            searchDto.setPage(null);
            searchDto.setPageSize(null);
            searchDto.setOffset(null);

            // 데이터 조회
            return productDao.searchProducts(searchDto);

        } catch (Exception e) {
            log.error("상품 검색 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("상품 검색 실패", e);
        }
    }
}