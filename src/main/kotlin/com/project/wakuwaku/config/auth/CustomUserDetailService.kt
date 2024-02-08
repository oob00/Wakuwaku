package com.project.wakuwaku.config.auth

import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(
    private val userRepository: UserRepository
): UserDetailsService {

    /**
     * spring security 에서 db의 회원 정보를 조회해서 로그인 시도한 회원의 정보가 있는지 확인
     */
    override fun loadUserByUsername(userId: String): UserDetails {
        val user: Users = userRepository.findById(userId)
            .orElseThrow { UsernameNotFoundException("존재하지 않는 회원입니다.") }

        return User.builder()
            .username(user.id)
            .password(user.password)
            .build()
    }

}