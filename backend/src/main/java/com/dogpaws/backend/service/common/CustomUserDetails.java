package com.dogpaws.backend.service.common;

import com.dogpaws.backend.entity.ajy.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final String nickname;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 유저(User) 엔티티를 기반으로 생성
     */
    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.authorities = List.of(user::getRole);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }


}
