package com.project.wakuwaku

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class WakuwakuApplication

fun main(args: Array<String>) {
	runApplication<WakuwakuApplication>(*args)
}
