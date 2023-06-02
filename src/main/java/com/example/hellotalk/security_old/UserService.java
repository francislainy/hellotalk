// package com.example.hellotalk.security;
//
// import com.example.hellotalk.entity.user.UserEntity;
// import com.example.hellotalk.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
//
// @Service
// @RequiredArgsConstructor
// public class UserService implements UserDetailsService {
//
// private final UserRepository userRepository;
//
// private final PasswordEncoder passwordEncoder;
//
// @Override
// public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
// UserEntity user = userRepository.findByUsername(username);
// if (user == null) {
// throw new UsernameNotFoundException("User with username '" + username + "' does not exist.");
// }
// // Use the passwordEncoder bean to encode or verify the user's password
// String encodedPassword = passwordEncoder.encode(user.getPassword());
// user.setPassword(encodedPassword);
// return user;
// }
// }
//
//
