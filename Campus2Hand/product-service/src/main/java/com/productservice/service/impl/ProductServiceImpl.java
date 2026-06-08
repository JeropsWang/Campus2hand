package com.productservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.productservice.entity.dto.req.ProductCreateReq;
import com.productservice.entity.dto.req.ProductListReq;
import com.productservice.entity.dto.resp.ProductResp;
import com.productservice.entity.po.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.service.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 吴绍祯
 * @since 2026-06-08
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Override
    public ProductResp createProduct(ProductCreateReq req) {
        Product product = new Product();
        BeanUtils.copyProperties(req, product);
        product.setPublishTime(LocalDateTime.now());
        product.setStatus("ACTIVE");
        save(product);
        return convertToResp(product);
    }

    @Override
    public ProductResp getProductById(Integer productId) {
        Product product = getById(productId);
        if (product == null) {
            return null;
        }
        return convertToResp(product);
    }

    @Override
    public IPage<ProductResp> listProducts(ProductListReq req) {
        Page<Product> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        if (req.getCategory() != null && !req.getCategory().isEmpty()) {
            queryWrapper.eq(Product::getCategory, req.getCategory());
        }
        if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            queryWrapper.like(Product::getTitle, req.getTitle());
        }
        queryWrapper.eq(Product::getStatus, "ACTIVE");
        queryWrapper.orderByDesc(Product::getPublishTime);
        IPage<Product> productPage = page(page, queryWrapper);
        return productPage.convert(this::convertToResp);
    }

    private ProductResp convertToResp(Product product) {
        ProductResp resp = new ProductResp();
        BeanUtils.copyProperties(product, resp);
        return resp;
    }
}