package com.productservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.productservice.entity.dto.ProductCreateDTO;
import com.productservice.entity.dto.ProductListDTO;
import com.productservice.entity.vo.ProductVO;
import com.productservice.entity.po.Product;
import com.productservice.entity.vo.UploadResponseVO;
import com.productservice.mapper.ProductMapper;
import com.productservice.service.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Value("${avatar.upload.path:./uploads/avatars}")
    private String uploadPath;


    @Override
    public ProductVO createProduct(ProductCreateDTO req) {
        Product product = new Product();
        BeanUtils.copyProperties(req, product);
        product.setPublishTime(LocalDateTime.now());
        product.setStatus("ACTIVE");
        save(product);
        return convertToResp(product);
    }

    @Override
    public ProductVO getProductById(Integer productId) {
        Product product = getById(productId);
        if (product == null) {
            return null;
        }
        return convertToResp(product);
    }

    @Override
    public IPage<ProductVO> listProducts(ProductListDTO req) {

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

    @Override
    public UploadResponseVO uploadAvatar(Long productId, MultipartFile file) {
        // 检查用户是否存在
        Product product = getById(productId);
        if (product == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查文件
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".png";

        // 使用用户ID命名头像文件（格式：用户ID.扩展名）
        String newFilename = productId + extension;

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 删除旧头像文件（如果存在）
            deleteOldAvatar(product.getImageUrl(), uploadDir);

            // 保存新文件
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 构建访问URL（使用相对路径，前端通过代理访问）
            String avatarUrl = "/avatars/" + newFilename;

            // 更新用户头像（存储相对路径）
            product.setImageUrl(avatarUrl);
            updateById(product);

            // 返回结果
            UploadResponseVO responseVO = new UploadResponseVO();
            responseVO.setUrl(avatarUrl);
            responseVO.setFilename(newFilename);

            return responseVO;

        } catch (IOException e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }


    /**
     * 删除旧头像文件
     * @param oldAvatarUrl 旧头像URL（可能是完整URL或相对路径）
     * @param uploadDir 上传目录路径
     */
    private void deleteOldAvatar(String oldAvatarUrl, Path uploadDir) {
        if (oldAvatarUrl == null || oldAvatarUrl.isEmpty()) {
            return;
        }

        try {
            // 提取文件名（支持完整URL和相对路径）
            String filename;
            if (oldAvatarUrl.contains("/")) {
                filename = oldAvatarUrl.substring(oldAvatarUrl.lastIndexOf("/") + 1);
            } else {
                filename = oldAvatarUrl;
            }

            // 构建旧文件路径
            Path oldFilePath = uploadDir.resolve(filename);

            // 如果文件存在，删除它
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
                System.out.println("Deleted old avatar: " + filename);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete old avatar: " + e.getMessage());
            // 删除失败不影响新头像上传
        }
    }

    private ProductVO convertToResp(Product product) {
        ProductVO resp = new ProductVO();
        BeanUtils.copyProperties(product, resp);
        return resp;
    }
}