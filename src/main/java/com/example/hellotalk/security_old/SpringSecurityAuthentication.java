// package com.example.hellotalk.security;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.factory.PasswordEncoderFactories;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// @Configuration
// @EnableWebSecurity
// public class SpringSecurityAuthentication {
// @Bean
// public PasswordEncoder passwordEncoder() {
// return PasswordEncoderFactories.createDelegatingPasswordEncoder();
// }
//
// @Bean
// public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
// DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
// provider.setUserDetailsService(userDetailsService);
// return provider;
// }
// }
