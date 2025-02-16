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
import java.util.List;
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

        if(optionDtos != null && !optionDtos.isEmpty()) {
            log.info("  {} 개 상품 옵션 생성중 ", optionDtos.size());
            List<ProductOption> productOptions = optionDtos.stream()
                    .map(optionDto -> ProductOption.builder()
                            .product(finalProduct)
                            .optionName(optionDto.getOptionName())
                            .optionPrice(optionDto.getOptionPrice())
                            .optionStock(optionDto.getOptionStock())
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
    public ProductDto getProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return convertProductToProductDto(product);
    }

    /**
     * 상품 목록 조회 (MyBatis + JPA 페이징)
     */
    @Transactional(readOnly = true)
    public Page<ProductListDto> getProducts(ProductSearchDto productSearchDto){
        int totalCount = productDao.getTotalCount(productSearchDto);

        List<ProductListDto> productList = productDao.getProducts(productSearchDto);

        return new PageImpl<>(
                productList,
                PageRequest.of(
                        productSearchDto.getPage() - 1,
                        productSearchDto.getPageSize()
                ),
                totalCount
        );
    }

    /**
     * 상품 수정 (JPA)
     * TODO : 재고관리 로직
     */
    @Transactional
    public void updateProduct(Integer productId, ProductDto productDto, MultipartFile thumbnailImage, MultipartFile detailImage, String userId) throws IOException {
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
    public void deleteProduct(Integer productId) {
        productRepository.deleteById(productId);
        log.info("productId : {}  삭제 성공", productId);
    }

    /**
     * 옵션 추가 (JPA)
     */
    public void addProductOption(Integer productId, ProductOptionDto productOptionDto) {
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
                .build();
        log.info("product id : {}, 옵션 추가 성공 , 옵션 이름 : {}", productId, productOption.getOptionName());
        productOptionRepository.save(productOption);
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
        productOptionDto.setOptionId(productOption.getOptionId());
        productOptionDto.setOptionName(productOption.getOptionName());
        productOptionDto.setOptionPrice(productOption.getOptionPrice());
        productOptionDto.setOptionStock(productOption.getOptionStock());
        return productOptionDto;
    }
}