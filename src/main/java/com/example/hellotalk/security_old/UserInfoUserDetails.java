// package com.example.hellotalk.security;
//
// import com.example.hellotalk.entity.user.UserEntity;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
//
// import java.util.Collection;
// import java.util.List;
//
// public class UserInfoUserDetails implements UserDetails {
//
// private String username;
// private String password;
// private List<GrantedAuthority> authorities;
//
// public UserInfoUserDetails(UserEntity userEntity) {
// username = userEntity.getUsername();
// password = userEntity.getPassword();
// authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
//
//// authorities = Arrays.stream(userEntity.getRoles().split(","))
//// .map(SimpleGrantedAuthority::new)
//// .collect(Collectors.toList());
// }
//
// @Override
// public Collection<? extends GrantedAuthority> getAuthorities() {
// return authorities;
// }
//
// @Override
// public String getUsername() {
// return username;
// }
//
// @Override
// public String getPassword() {
// return password;
// }
//
// @Override
// public boolean isAccountNonExpired() {
// return true;
// }
//
// @Override
// public boolean isAccountNonLocked() {
// return true;
// }
//
// @Override
// public boolean isCredentialsNonExpired() {
// return true;
// }
//
// @Override
// public boolean isEnabled() {
// return true;
// }
// }
