package com.project.wakuwaku.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.project.wakuwaku.model.mongo","com.project.wakuwaku.chat"])
@EnableMongoAuditing
class MongoConfig {
}