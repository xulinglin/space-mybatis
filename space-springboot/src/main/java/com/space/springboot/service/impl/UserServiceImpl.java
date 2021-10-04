package com.space.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.space.core.annotation.Authority;
import com.space.springboot.mapper.UserMapper;
import com.space.springboot.entity.User;
import com.space.springboot.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户表(User)表服务实现类
 *
 * @author xulinglin
 * @since 2021-09-28
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //权限管理由mybatis 拦截器管理

    /**
     * mybatis puls 执行
     * @param page
     * @param user
     * @return
     */
    @Override
    @Authority
    public IPage<User> pageService(Page<User> page, User user) {
        return this.page(page, new QueryWrapper<User>(user));
    }

    /**
     * 手动调用 Mapper 文件执行
     * @param user
     * @return
     */
    @Override
    @Authority
    public List<User> selectMapper(User user) {
        return baseMapper.selectMapper(user);
    }
}

