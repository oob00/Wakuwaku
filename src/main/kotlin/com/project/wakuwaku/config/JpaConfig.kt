package com.project.wakuwaku.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.project.wakuwaku.model.jpa"])
class JpaConfig {
}