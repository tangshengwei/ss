package com.deallinker.ss.base.service.impl;

import com.deallinker.ss.base.entity.SysPermission;
import com.deallinker.ss.base.mapper.SysPermissionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deallinker.ss.base.service.SysPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author tangsw
 * @since 2020-12-03
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Override
    public List<SysPermission> findByUserId(Long userId) {
        if(userId == null) {
            return null;
        }
        List<SysPermission> permissionList = baseMapper.selectPermissionByUserId(userId);
        // 如果没有权限，则将集合中的数据null移除
//        permissionList.remove(null);
        return permissionList;
    }
}
