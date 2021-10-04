package com.space.springboot.page;

import com.google.common.collect.Lists;
import com.space.core.auth.AuthorityInterface;

import java.util.List;

/**
 * @author xulinglin
 */
public class AuthorityInterfaceImpl implements AuthorityInterface {

    @Override
    public List<String> get() {
        /**
         * 这里模仿,用当前登录用户取组织id。
         */
        return Lists.newArrayList("1","2");
    }

}
