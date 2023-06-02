// package com.example.hellotalk.security;
//
// import com.example.hellotalk.entity.user.UserEntity;
// import com.example.hellotalk.exception.UserNotFoundException;
// import com.example.hellotalk.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
//
// import java.util.Optional;
//
// @Component
// @RequiredArgsConstructor
// public class UserIfoUserDetailsService implements UserDetailsService {
//
// private final UserRepository userRepository;
//
// private final PasswordEncoder passwordEncoder;
//
// @Override
// public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
// Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
// return optionalUserEntity.map(user -> {
// String encodedPassword = passwordEncoder.encode(user.getPassword());
// user.setPassword(encodedPassword);
// return new UserInfoUserDetails(user);
// }).orElseThrow(() -> new UserNotFoundException("user not found " + username));
// }
// }
//
