package com.space.springboot.page;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.space.core.auth.AuthorityConfig;
import com.space.core.auth.AuthorityInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    /**
     * 权限配置,获取部门id
     * @return
     */
    @Bean
    public AuthorityInterface authorityInterface(){
        return new AuthorityInterfaceImpl();
    }

    /**
     * 权限插件
     * @return
     */
    @Bean
    public AuthorityConfig authorityConfig() {
//        return new AuthorityConfig("dept_id",new AuthorityInterfaceImpl()); 也可以直接用new方式,是否需要spring管理有开发人员控制
          return new AuthorityConfig("dept_id",authorityInterface());

    }

    /**
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
