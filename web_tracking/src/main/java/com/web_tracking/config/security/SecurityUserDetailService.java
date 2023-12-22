package com.web_tracking.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class SecurityUserDetailService implements UserDetailsService {

    @Autowired
    private AdminJwtRepository adminJwtRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> optional = adminJwtRepository.findByAdminId(username);
        if(optional.isEmpty()) {
            throw new UsernameNotFoundException(username + WebConstants.NON_USER);
        } else {
            Admin admin = optional.get();
            return new SecurityUser(admin);
        }

    }

}
