package com.medicalassistance.core.security;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(user.getUserId(), user.getEmailAddress(), user.getPassword(), mapToGrantedAuthorities(user.getAuthorities()), user.getLastPasswordResetDate());
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<AuthorityName> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toList());
    }
}
