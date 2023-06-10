package com.example.hellotalk.client;

import com.example.hellotalk.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DBClient {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<UserEntity> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserEntity.class));
    }
}
