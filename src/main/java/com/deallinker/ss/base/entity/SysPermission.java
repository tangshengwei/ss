package com.deallinker.ss.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author tangsw
 * @since 2020-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父权限 ID (0为顶级菜单)
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 授权标识符
     */
    private String code;

    /**
     * 授权路径
     */
    private String url;

    /**
     * 类型(1菜单，2按钮)
     */
    private Integer type;

    /**
     * 图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


}
