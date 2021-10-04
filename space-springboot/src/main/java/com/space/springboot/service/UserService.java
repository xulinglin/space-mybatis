package com.space.springboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.space.springboot.entity.User;

import java.util.List;

/**
 * 用户表(User)表服务接口
 *
 * @author xulinglin
 * @since 2021-09-28
 */
public interface UserService extends IService<User> {


    IPage<User> pageService(Page<User> page, User user);


    List<User> selectMapper(User user);
}

