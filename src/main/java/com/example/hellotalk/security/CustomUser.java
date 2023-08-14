package com.example.hellotalk.security;

import com.example.hellotalk.entity.user.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomUser extends User {

    private final transient UserEntity userEntity;

    public CustomUser(UserEntity userEntity, Collection<? extends GrantedAuthority> authorities) {
        super(userEntity.getUsername(), userEntity.getPassword(), authorities);
        this.userEntity = userEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }
}
