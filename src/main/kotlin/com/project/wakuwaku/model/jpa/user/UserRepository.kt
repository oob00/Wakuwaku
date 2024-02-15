package com.project.wakuwaku.model.jpa.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<Users, Long> {
    fun findById(id: String): Optional<Users>

    fun existsById(id: String): Boolean
}