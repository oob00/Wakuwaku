package com.project.wakuwaku.model.jpa.friend

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FriendRepository : JpaRepository<Friend, Long> {
    fun findAllByIdAndIsFriend(id: String, isFriend: Boolean): List<Friend>

    fun findAllByFriendIdAndIsFriend(friendId: String, isFriend: Boolean): List<Friend>

    fun findById(id: String): Friend
}