package com.space.springboot.vo;

import com.space.core.annotation.FieldBind;
import com.space.springboot.entity.Role;
import com.space.springboot.mybatis.interceptor.UserNameInterceptor;
import lombok.Data;

@Data
public class RoleVo extends Role {

    //创建者
    @FieldBind(column = "createBy",interceptor = UserNameInterceptor.class)
    private String createName;

    //更新者
    @FieldBind(column = "updateBy",interceptor = UserNameInterceptor.class)
    private String updateName;
}
