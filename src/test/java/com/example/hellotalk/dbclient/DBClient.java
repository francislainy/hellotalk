package com.example.hellotalk.dbclient;

import com.example.hellotalk.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DBClient {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<UserEntity> getUserDBDataForList() {
        String sql = "SELECT * FROM users";
        List<UserEntity> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserEntity.class));
        if (users.isEmpty()) {
            // Handle the case where no rows were returned
            // For example, throw an exception or log an error message
            throw new RuntimeException("No rows were returned for query: " + sql);
        }
        return users;
    }

}
