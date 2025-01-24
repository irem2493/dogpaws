package com.dogpaws.backend.service.common;

import org.green.backend.entity.Company;
import org.green.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 유저(User) 엔티티를 기반으로 생성
     */
    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.authorities = List.of(user::getRole);
    }

    /**
     * 기업(Company) 엔티티를 기반으로 생성
     */
    public CustomUserDetails(Company company) {
        this.username = company.getUsername();
        this.password = company.getPassword();
        this.name = company.getName();
        this.authorities = List.of(company::getRole);
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

    public String getName(){
        return name;
    }

    /**
     * 계정 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부 (true: 잠금되지 않음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부 (true: 활성화됨)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
