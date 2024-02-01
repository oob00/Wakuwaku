package com.project.wakuwaku.model.jpa.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<Users, Long> {
}