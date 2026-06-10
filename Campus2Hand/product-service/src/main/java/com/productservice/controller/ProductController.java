package com.productservice.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.productservice.entity.dto.req.ProductCreateReq;
import com.productservice.entity.dto.req.ProductListReq;
import com.productservice.entity.dto.resp.ProductResp;
import com.productservice.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ProductResp> createProduct(@RequestBody ProductCreateReq req) {
        ProductResp product = productService.createProduct(req);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResp> getProductById(@PathVariable Integer productId) {
        ProductResp product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/list")
    public ResponseEntity<IPage<ProductResp>> listProducts(ProductListReq req) {
        IPage<ProductResp> products = productService.listProducts(req);
        return ResponseEntity.ok(products);
    }
}