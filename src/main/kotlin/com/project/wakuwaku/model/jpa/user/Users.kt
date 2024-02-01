package com.project.wakuwaku.model.jpa.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Users(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,
        nickname: String
) {
    var nickname: String = nickname
        private set

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }
}