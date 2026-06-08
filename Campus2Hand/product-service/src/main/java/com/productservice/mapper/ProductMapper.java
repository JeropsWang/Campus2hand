package com.productservice.mapper;

import com.productservice.entity.po.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 吴绍祯
 * @since 2026-06-08
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}
