package com.example.hellotalk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class Environment {

    @Value("${base.url}")
    private String baseUrl;
}
