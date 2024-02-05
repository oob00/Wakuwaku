package com.project.wakuwaku.config.auth

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity) : SecurityFilterChain {
        http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .cors { it.disable() }
            //.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                it.anyRequest().authenticated()
            }
            .formLogin {
                it
                    .loginPage("/login")
                    .loginProcessingUrl("/check")
                    .usernameParameter("id")
                    .passwordParameter("pw")
                    .defaultSuccessUrl("/index", true)
                    .permitAll()
            }

        return http.build()
    }
}