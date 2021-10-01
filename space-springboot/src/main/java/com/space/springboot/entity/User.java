package com.space.springboot.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.space.core.annotation.FieldBind;
import com.space.springboot.mybatis.interceptor.RoleNameInterceptor;
import com.space.springboot.mybatis.interceptor.UserNameInterceptor;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 用户表(User)表实体类
 *
 * @author xulinglin
 * @since 2021-09-28
 */
@Data
@ToString
@SuppressWarnings("serial")
public class User extends Model<User> {
    //编号
    private String id;
    //用户名称
    private String name;
    //角色id
    private String roleId;
    //创建者
    @FieldBind(column = "createBy",interceptor = UserNameInterceptor.class)
    private String createBy;
    //创建时间
    private Date createTime;
    //更新者
    @FieldBind(column = "updateBy",interceptor = UserNameInterceptor.class)
    private String updateBy;
    //更新时间
    private Date updateTime;

    //角色名称
    @FieldBind(column = "roleId",interceptor = RoleNameInterceptor.class)
    @TableField(exist = false)
    private String roleName;



}

