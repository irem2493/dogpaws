package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.common.FileDto;
import com.dogpaws.backend.dto.rim.ProductDto;
import com.dogpaws.backend.dto.rim.ProductListDto;
import com.dogpaws.backend.dto.rim.ProductOptionDto;
import com.dogpaws.backend.dto.rim.ProductSearchDto;
import com.dogpaws.backend.entity.rim.Product;
import com.dogpaws.backend.entity.rim.ProductOption;
import com.dogpaws.backend.repository.dao.rim.ProductDao;
import com.dogpaws.backend.repository.jpa.rim.ProductOptionRepository;
import com.dogpaws.backend.repository.jpa.rim.ProductRepository;
import com.dogpaws.backend.utils.FileUploadUtil;
import com.dogpaws.backend.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    private final ProductDao productDao;

    private final FileUploadUtil fileUploadUtil;

    /**
     * 상품 등록 (JPA)
     */
    public void registProduct(ProductDto productDto, List<ProductOptionDto> optionDtos, MultipartFile thumbnailImage, MultipartFile detailImage, String userId) throws IOException {
        log.info("상품 등록 시작. 상품명: {}, 사용자: {}", productDto.getName(), userId);

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
                .optionPrice(0)
                .optionStock(finalProduct.getStockQuantity())
                .isBaseOption(true)
                .build();
        productOptionRepository.save(baseOption);

        if(optionDtos != null && !optionDtos.isEmpty()) {
            log.info("  {} 개 상품 옵션 생성중 ", optionDtos.size());
            List<ProductOption> productOptions = optionDtos.stream()
                    .map(optionDto -> ProductOption.builder()
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
                            .build())
                    .collect(Collectors.toList());
            productOptionRepository.saveAll(productOptions);  // 옵션 저장 추가
            log.debug("상품 옵션 생성됨: {}", productOptions);
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

    /**
     * 상품 목록 조회 (MyBatis + JPA 페이징)
     */
    @Transactional(readOnly = true)
    public Page<ProductListDto> getProducts(String category, int page, int size) {
        ProductSearchDto searchDto = new ProductSearchDto();
        searchDto.setMainCategory(category);
        searchDto.setPage(page + 1);
        searchDto.setPageSize(size);
        searchDto.setStatus("O"); // 판매중인 상품만 조회

        return searchProducts(searchDto);
    }

    public Page<ProductListDto> searchProducts(ProductSearchDto searchDto) {
        // offset 계산
        searchDto.setOffset((searchDto.getPage() - 1) * searchDto.getPageSize());

        try {
            // 데이터 조회
            List<ProductListDto> content = productDao.searchProducts(searchDto);
            int total = productDao.getTotalCount(searchDto);

            log.info("content : {}", content);
            // Page 객체 생성 및 반환
            return new PageImpl<>(content,
                    PageRequest.of(searchDto.getPage() - 1, searchDto.getPageSize()),
                    total);

        } catch (Exception e) {
            log.error("상품 검색 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("상품 검색 실패", e);
        }
    }

    /**
     * 상품 수정 (JPA)
     * TODO : 재고관리 로직
     */
    @Transactional
    public void updateProduct(Long productId, ProductDto productDto, MultipartFile thumbnailImage, MultipartFile detailImage, String userId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        product.update(
                productDto.getName(),
                productDto.getPrice(),
                productDto.getStockQuantity(),
                productDto.getDescription(),
                productDto.getStatus(),
                productDto.getMainCategory(),
                productDto.getSubCategory(),
                productDto.getMaterial(),
                productDto.getOrigin(),
                StringUtil.stringToLocalDate(productDto.getExpirationDate()),
                productDto.getWeight()
        );

        // 썸네일 이미지 처리
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            // 기존 이미지가 있다면 삭제
            if (product.getImageUrl() != null) {
                fileUploadUtil.deleteFile(product.getImageUrl());
            }

            // 새 이미지 저장
            FileDto thumbnailFile = fileUploadUtil.saveFile(
                    thumbnailImage, "PM",
                    productId.toString(),
                    userId
            );
            product.updateImages(thumbnailFile.getFileUrl(), product.getImageDetailUrl());
        }

        // 상세 이미지 처리
        if (detailImage != null && !detailImage.isEmpty()) {
            // 기존 이미지가 있다면 삭제
            if (product.getImageDetailUrl() != null) {
                fileUploadUtil.deleteFile(product.getImageDetailUrl());
            }

            // 새 이미지 저장
            FileDto detailFile = fileUploadUtil.saveFile(
                    detailImage, "PD",
                    productId.toString(),
                    userId
            );
            product.updateImages(
                    product.getImageUrl(),
                    detailFile.getFileUrl()
            );
        }
    }

    /**
     * 상품 삭제 (JPA)
     */
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
        log.info("productId : {}  삭제 성공", productId);
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
            productDto.setOrigin(product.getOrigin());
            productDto.setExpirationDate(product.getExpirationDate().toString());
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
    public Map<String, List<ProductListDto>> getAllBestProducts(int size) {
        Map<String, List<ProductListDto>> result = new HashMap<>();

        // 각 카테고리별 베스트 상품 조회
        result.put("bestFoods", productDao.getBestProducts("F", size));
        result.put("bestSnacks", productDao.getBestProducts("N", size));
        result.put("bestToys", productDao.getBestProducts("T", size));

        return result;
    }
}