package com.project.wakuwaku.model.mongo

import com.project.wakuwaku.model.mongo.Chatting
import org.springframework.data.mongodb.repository.MongoRepository

interface ChatRepository : MongoRepository<Chatting, String> {
}