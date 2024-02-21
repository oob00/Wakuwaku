package com.project.wakuwaku.auth.dto

data class RegistUserDto(
    val id: String,
    val password: String,
    val email: String,
    val name: String,
    val nickname: String,
)
