package com.space.springboot.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色表(Role)表实体类
 *
 * @author xulinglin
 * @since 2021-09-28
 */
@ToString
@Data
@SuppressWarnings("serial")
public class Role extends Model<Role> {
    //编号
    private String id;
    //角色名称
    private String name;
    //创建者
    private String createBy;
    //创建时间
    private Date createTime;
    //更新者
    private String updateBy;
    //更新时间
    private Date updateTime;

}

