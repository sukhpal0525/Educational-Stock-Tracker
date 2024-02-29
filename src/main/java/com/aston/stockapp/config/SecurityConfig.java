package com.aston.stockapp.config;

import com.aston.stockapp.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
// Disable auth
//            .authorizeRequests()
//                .antMatchers("/", "/login", "/register").permitAll()
//                .anyRequest().authenticated()
//                .and()
//            .formLogin()
//                .loginPage("/login")
//                .failureUrl("/login?error=true")
//                .defaultSuccessUrl("/", true)
//                .failureUrl("/login?error=true")
//                .permitAll()yws
//                .and()
//            .logout()
//                .permitAll();

//Old (Working)
//                    .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/", "/login", "/register", "/css/**", "/js/**", "/icons/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/portfolio/add").authenticated() // Only authenticate POST requests to /portfolio/add
//                .anyRequest().permitAll() // All other requests are permitted without authentication
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/", true)
//                .failureUrl("/login?error=true")
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll();

                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/", "/login", "/register", "/css/**", "/js/**", "/icons/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/portfolio/add").authenticated() // Only authenticate POST requests to /portfolio/add
                    .anyRequest().permitAll() // All other requests are permitted without authentication
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    // Removed the defaultSuccessUrl so Spring Security can redirect to the previous url
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll();

            return http.build();
        }
}