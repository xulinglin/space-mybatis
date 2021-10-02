package com.space.springboot.mybatis.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.space.core.Interceptor;
import com.space.springboot.entity.User;
import com.space.springboot.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserNameInterceptor extends Interceptor<String, String> {

    @Resource
    private UserService userService;

    @Override
    public Map<String, String> get(Collection<?> coll) {
        List<User> list = userService.list(Wrappers.<User>lambdaQuery().in(User::getId, coll));
        return list.stream().collect(Collectors.toMap(User::getId, User::getName));
    }
}
