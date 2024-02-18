package com.project.wakuwaku.config.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthFilter(
    private val jwtUtil: JwtUtil
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 헤더에서 JWT 를 받아옵니다.
        val token: String? = jwtUtil.resolveToken((request))
        // 유효한 토큰인지 확인합니다.
        if (token != null && jwtUtil.validateToken(token)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            val authentication = jwtUtil.getAuthentication(token)
            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

}