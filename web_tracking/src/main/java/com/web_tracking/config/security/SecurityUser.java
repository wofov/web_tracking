package com.web_tracking.config.security;


import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class SecurityUser extends User {
    private final Admin admin;

    public SecurityUser(Admin admin) {

        super(admin.getAdminId(), admin.getPassword(),
                AuthorityUtils.createAuthorityList(admin.getRole().toString()));
        this.admin = admin;
    }
}
