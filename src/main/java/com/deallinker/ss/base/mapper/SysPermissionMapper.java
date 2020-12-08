package com.deallinker.ss.base.mapper;

import com.deallinker.ss.base.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author tangsw
 * @since 2020-12-03
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

        List<SysPermission> selectPermissionByUserId(@Param("userId") Long userId);
}
