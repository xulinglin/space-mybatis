package com.space.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.space.core.annotation.ExecutedBind;
import com.space.core.asm.AsmUtils;
import com.space.springboot.mapper.RoleMapper;
import com.space.springboot.entity.Role;
import com.space.springboot.service.RoleService;
import com.space.springboot.vo.RoleVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色表(Role)表服务实现类
 *
 * @author xulinglin
 * @since 2021-09-28
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}

