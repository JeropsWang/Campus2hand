package com.productservice.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.productservice.entity.dto.ProductCreateDTO;
import com.productservice.entity.dto.ProductListDTO;
import com.productservice.entity.vo.ProductVO;
import com.productservice.entity.po.Product;
import com.productservice.entity.vo.UploadResponseVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 吴绍祯
 * @since 2026-06-08
 */
public interface IProductService extends IService<Product> {

    ProductVO createProduct(ProductCreateDTO req);

    ProductVO getProductById(Integer productId);

    IPage<ProductVO> listProducts(ProductListDTO req);

    UploadResponseVO uploadAvatar(Long productId, MultipartFile file);
}