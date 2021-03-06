package com.space.springboot.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.space.core.annotation.ExecutedBind;
import com.space.core.asm.AsmUtils;
import com.space.springboot.entity.Role;
import com.space.springboot.service.RoleService;
import com.space.springboot.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 角色表(Role)表控制层
 *
 * @author xulinglin
 * @since 2021-09-28
 */
@RestController
@RequestMapping("role")
public class RoleController extends ApiController {
    /**
     * 服务对象
     */
    @Autowired
    private RoleService roleService;

    /**
     * 分页查询所有数据
     * @param role 查询实体
     * @return 所有数据
     */
    @GetMapping("/list")
    @ExecutedBind
    public R selectAll(Role role) {
        List<Role> list = this.roleService.list(new QueryWrapper<Role>(role));
        return success(AsmUtils.copyBeanList(list, RoleVo.class,Boolean.TRUE));
    }



    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.roleService.getById(id));
    }

}

