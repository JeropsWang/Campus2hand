package com.productservice.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.productservice.entity.dto.ProductCreateDTO;
import com.productservice.entity.dto.ProductListDTO;
import com.productservice.entity.vo.ProductVO;
import com.productservice.entity.vo.UploadResponseVO;
import com.productservice.service.IProductService;
import com.productservice.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 吴绍祯
 * @since 2026-06-08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<ProductVO> createProduct(@RequestBody ProductCreateDTO req) {
        ProductVO product = productService.createProduct(req);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductVO> getProductById(@PathVariable Integer productId) {
        ProductVO product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/list")
    public ResponseEntity<IPage<ProductVO>> listProducts(ProductListDTO req) {
        IPage<ProductVO> products = productService.listProducts(req);
        return ResponseEntity.ok(products);
    }

    /**
     * 上传头像
     */
    @PostMapping("/{productId}/avatar")
    public Result<UploadResponseVO> uploadAvatar(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        UploadResponseVO response = productService.uploadAvatar(productId, file);
        return Result.success(response);
    }


}