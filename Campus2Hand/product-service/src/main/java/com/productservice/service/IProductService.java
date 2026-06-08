package com.productservice.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.productservice.entity.dto.req.ProductCreateReq;
import com.productservice.entity.dto.req.ProductListReq;
import com.productservice.entity.dto.resp.ProductResp;
import com.productservice.entity.po.Product;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 吴绍祯
 * @since 2026-06-08
 */
public interface IProductService extends IService<Product> {

    ProductResp createProduct(ProductCreateReq req);

    ProductResp getProductById(Integer productId);

    IPage<ProductResp> listProducts(ProductListReq req);

}