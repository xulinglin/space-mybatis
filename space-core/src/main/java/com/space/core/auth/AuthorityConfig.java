package com.space.core.auth;

import javax.annotation.Resource;

/**
 * @author xulinglin
 */
public class AuthorityConfig {

    private String column;

    private Boolean auth = Boolean.FALSE;

    @Resource
    private AuthorityInterface authorityInterface;

    private AuthorityConfig(){

    }

    public AuthorityConfig(String column, AuthorityInterface authority) {
        this.column = column;
        this.auth = Boolean.TRUE;
        this.authorityInterface = authority;
    }

    public String getColumn() {
        return column;
    }

    public Boolean getAuth() {
        return auth;
    }

    public AuthorityInterface getAuthorityInterface() {
        return authorityInterface;
    }
}
