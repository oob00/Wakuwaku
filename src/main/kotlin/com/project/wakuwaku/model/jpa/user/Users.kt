package com.project.wakuwaku.model.jpa.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
class Users(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val seq: Long = 0L,
    val id: String,
    var password: String,
    val userType: Int,  // wakuwaku=1, kakao=2
    name: String,
    nickname: String,
    @CreatedDate
    val createDt: LocalDateTime,
    @LastModifiedDate
    var updateDt: LocalDateTime
) {
    var name: String = name
        private set
    var nickname: String = nickname
        private set

    fun updateName(name: String) {
        this.name = name
    }
    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }
}