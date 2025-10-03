// Generic Security Patch Applied
// Fix ID: fix-1759534179336
// Applied: 2025-10-03T23:29:39.336Z

package com.example.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().and()
            .headers()
                .frameOptions().deny()
                .contentTypeOptions().and()
                .xssProtection().and()
            .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false);
    }
}