package com.project.wakuwaku.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/user")
@RestController
class UserController {

    @PostMapping("/info")
    fun getUserInfo(): ResponseEntity<String> {
        return ResponseEntity.ok("success")
    }
}