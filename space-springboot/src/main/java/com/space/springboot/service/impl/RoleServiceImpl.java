package com.space.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.space.core.annotation.ExecutedBind;
import com.space.core.asm.ASMUtils;
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

    @Override
    @ExecutedBind
    public List<RoleVo> get(List<Role> list){
        List<RoleVo> listVo = new ArrayList<>();
        for (Role value: list) {
            RoleVo vo = new RoleVo();
            ASMUtils.copyProperties(vo,value);
            listVo.add(vo);
        }
        return listVo;
    }
}

