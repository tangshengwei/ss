package com.deallinker.ss.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author tangsw
 * @since 2020-12-01
 */
@Data
public class SysUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码，加密存储, admin/1234
     */
    private String password;

    /**
     * 帐户是否过期(1 未过期，0已过期)
     */
//    private Integer isAccountNonExpired;

    /**
     * 帐户是否被锁定(1 未过期，0已过期)
     */
//    private Integer isAccountNonLocked;

    /**
     * 密码是否过期(1 未过期，0已过期)
     */
//    private Integer isCredentialsNonExpired;

    /**
     * 帐户是否可用(1 可用，0 删除用户)
     */
//    private Integer isEnabled;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 注册手机号
     */
    private String mobile;

    /**
     * 注册邮箱
     */
    private String email;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }

    /**
     * 帐户是否有效：1 未过期，0已过期
     * 1 true
     * 0 false
     */
    private boolean isAccountNonExpired = false;
    private boolean isAccountNonLocked = false;
    private boolean isCredentialsNonExpired = false;
    private boolean isEnabled = false;

    /**
     * 它不是sys_user表中的属性，所以要进行标识，不然mybatis-plus会报错
     */
    @TableField(exist = false)
    private Collection<? extends GrantedAuthority> authorities;
}
