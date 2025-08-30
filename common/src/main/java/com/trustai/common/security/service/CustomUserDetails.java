package com.trustai.common.security.service;

import com.trustai.common.domain.user.Role;
import com.trustai.common.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;
    private final User.AccountStatus accountStatus;
    private final String email;
    private final String mobile;
    private final Set<Role> roles;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.getAccountStatus() == User.AccountStatus.ACTIVE;
        this.roles = user.getRoles() != null ? user.getRoles() : Collections.emptySet();
        this.authorities = roles.stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        this.accountStatus = user.accountStatus;
        this.email = user.getEmail();
        this.mobile = user.getMobile();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
