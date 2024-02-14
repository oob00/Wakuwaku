package com.project.wakuwaku.config.auth

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

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
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                it.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                it.anyRequest().authenticated()
            }
            .formLogin {
                it
                    .loginPage("/login")
                    .loginProcessingUrl("/check")
                    .usernameParameter("id")
                    .passwordParameter("pw")
                    .defaultSuccessUrl("/chat", true)
                    .permitAll()
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:8080") // 허용할 출처
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메소드
        configuration.allowedHeaders = listOf("*") // 허용할 헤더
        configuration.allowCredentials = true // 쿠키/인증 정보 포함 여부
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // 모든 경로에 대해 위 설정 적용
        return source
    }
}