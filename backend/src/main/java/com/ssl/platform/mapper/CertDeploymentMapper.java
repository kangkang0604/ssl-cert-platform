package com.ssl.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ssl.platform.entity.CertDeployment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部署记录Mapper接口
 */
@Mapper
public interface CertDeploymentMapper extends BaseMapper<CertDeployment> {
}
