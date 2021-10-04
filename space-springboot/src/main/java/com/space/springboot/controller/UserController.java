package com.space.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.space.springboot.entity.User;
import com.space.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 用户表(User)表控制层
 *
 * @author xulinglin
 * @since 2021-09-28
 */
@RestController
@RequestMapping("user")
public class UserController extends ApiController {
    /**
     * 服务对象
     */
    @Autowired
    private UserService userService;

    /**
     * 分页查询所有数据 Mybatis puls
     *
     * @param page 分页对象
     * @param user 查询实体
     * @return 所有数据
     */
    @GetMapping("/list")
    public R selectAll(Page<User> page, User user) {
        return success(userService.pageService(page,user));
    }

    /**
     * 分页查询所有数据 mybatis
     *
     * @param user 查询实体
     * @return 所有数据
     */
    @GetMapping("/listMapper")
    public R listMapper(User user) {
        return success(userService.selectMapper(user));
    }

    @GetMapping("/count")
    public R count() {
        return success(this.userService.count(Wrappers.lambdaQuery()));
    }


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.userService.getById(id));
    }

}

