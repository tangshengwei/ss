package com.deallinker.ss.base.service;

import com.deallinker.ss.base.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author tangsw
 * @since 2020-12-03
 */
public interface SysPermissionService extends IService<SysPermission> {

    public List<SysPermission> findByUserId(Long userId);
}
