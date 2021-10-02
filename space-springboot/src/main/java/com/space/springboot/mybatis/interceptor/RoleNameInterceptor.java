package com.space.springboot.mybatis.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.space.core.Interceptor;
import com.space.springboot.entity.Role;
import com.space.springboot.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleNameInterceptor extends Interceptor<String, String> {

    @Resource
    private RoleService roleService;

    public Map<String, String> get(Collection<?> coll) {
        List<Role> list = roleService.list(Wrappers.<Role>lambdaQuery().in(Role::getId,coll));
        return list.stream().collect(Collectors.toMap(Role::getId, Role::getName));
    }

}
