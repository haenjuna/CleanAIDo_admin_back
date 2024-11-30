package org.zerock.cleanaido_admin_back.product.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.cleanaido_admin_back.common.dto.PageRequestDTO;
import org.zerock.cleanaido_admin_back.common.dto.PageResponseDTO;
import org.zerock.cleanaido_admin_back.common.dto.SearchDTO;
import org.zerock.cleanaido_admin_back.common.dto.UploadDTO;
import org.zerock.cleanaido_admin_back.category.dto.CategoryDTO;
import org.zerock.cleanaido_admin_back.product.dto.ProductListDTO;
import org.zerock.cleanaido_admin_back.product.dto.ProductReadDTO;
import org.zerock.cleanaido_admin_back.product.dto.ProductRegisterDTO;
import org.zerock.cleanaido_admin_back.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@Log4j2
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("list")
    public ResponseEntity<PageResponseDTO<ProductListDTO>> list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword) {

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        log.info("UserId: " + userId);
        log.info("Role: " + role);

        SearchDTO searchDTO = SearchDTO.builder()
                .searchType(type)
                .keyword(keyword)
                .build();

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .searchDTO(searchDTO)
                .build();

        return ResponseEntity.ok(productService.listProductByRole(pageRequestDTO, userId, role));
    }


    @GetMapping("seller/category")
    public ResponseEntity<List<CategoryDTO>> searchCategory(
            @RequestParam(value = "keyword", required = false) String keyword) {

        return ResponseEntity.ok(productService.searchCategory(keyword));
    }



    @PostMapping(value = "seller/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> registerProduct(
            @ModelAttribute ProductRegisterDTO productRegisterDTO,
            @RequestParam List<Long> categoryList,
            @RequestParam("imageFiles") MultipartFile[] imageFiles,
            @RequestParam("detailImageFiles") MultipartFile[] detailImageFiles,
            @RequestParam("usageImageFiles") MultipartFile[] usageImageFiles) {

        // 인증 정보 가져오기
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // DTO에 seller 설정
        productRegisterDTO.setSeller(userId);

        log.info("ProductRegisterDTO: " + productRegisterDTO);
        log.info("CategoryList: " + categoryList);

        // 파일 업로드 DTO 생성
        UploadDTO imageUploadDTO = new UploadDTO(imageFiles, null);
        UploadDTO detailImageUploadDTO = new UploadDTO(detailImageFiles, null);
        UploadDTO usageImageUploadDTO = new UploadDTO(usageImageFiles, null);

        // 서비스 호출로 상품 등록
        Long productId = productService.registerProduct(
                productRegisterDTO,
                categoryList,
                imageUploadDTO,
                detailImageUploadDTO,
                usageImageUploadDTO
        );

        return ResponseEntity.ok(productId);
    }





    @GetMapping("/read/{pno}")
    public ResponseEntity<ProductReadDTO> read(@PathVariable Long pno) {
        ProductReadDTO readDTO = productService.getProduct(pno);
        return ResponseEntity.ok(readDTO);
    }

    @DeleteMapping("seller/delete")
    public Long delete(
            @RequestParam(value = "pno", required = false) Long pno
    ){
        return productService.deleteProduct(pno);
    }

    @PutMapping(value = "seller/{pno}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> update(
            @PathVariable Long pno, // 수정 대상 Product 번호
            @ModelAttribute ProductRegisterDTO productRegisterDTO, // 수정할 기본 정보
            @RequestParam List<Long> categoryList, // 수정할 카테고리 리스트
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles, // 새 이미지 파일
            @RequestParam(value = "detailImageFiles", required = false) MultipartFile[] detailImageFiles, // 새 상세 이미지
            @RequestParam(value = "usageImageFiles", required = false) MultipartFile[] usageImageFiles) { // 새 사용법 이미지

        // 파일이 존재하면 UploadDTO 생성
        UploadDTO imageUploadDTO = (imageFiles != null) ? new UploadDTO(imageFiles, null) : null;
        UploadDTO detailImageUploadDTO = (detailImageFiles != null) ? new UploadDTO(detailImageFiles, null) : null;
        UploadDTO usageImageUploadDTO = (usageImageFiles != null) ? new UploadDTO(usageImageFiles, null) : null;

        // 서비스 호출을 통해 데이터 수정
        Long updatedPno = productService.updateProduct(
                pno, productRegisterDTO, categoryList, imageUploadDTO, detailImageUploadDTO, usageImageUploadDTO);

        return ResponseEntity.ok(updatedPno); // 수정된 Product 번호 반환
    }
}
