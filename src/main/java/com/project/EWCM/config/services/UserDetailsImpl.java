package com.project.EWCM.config.services;

import com.project.EWCM.DTO.AccountDto;
import com.project.EWCM.Document.Account;
import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails{
    private String id;

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String email;

    public UserDetailsImpl(String username, String password, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // Phương thức build
    public static UserDetailsImpl build(Account account) {
//        List<GrantedAuthority> authorities = account.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName())) // Chuyển đổi các quyền thành đối tượng GrantedAuthority
//                .toList();

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getType()));
        
        return new UserDetailsImpl(
                account.getUsername(),
                account.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getId(){
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }
}
