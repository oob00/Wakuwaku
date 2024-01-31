package com.project.wakuwaku.mongo

import org.bson.Document
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataMongoTest
@ExtendWith(SpringExtension::class)
class MongoDbEmbeddedTest {

    @Test
    @DisplayName("내부 MongoDb 확인 테스트")
    fun test(@Autowired mongoTemplate: MongoTemplate) {
        // given
        val objectToSave = Document().append("key", "value")

        // when
        mongoTemplate.save(objectToSave, "collection")

        // then
        assertThat(mongoTemplate.findAll(Document::class.java, "collection"))
                .extracting("key")
                .containsOnly("value")
    }

    @Test
    @DisplayName("테스트 시 내부 MongoDb 독립 여부 테스트")
    fun test2(@Autowired mongoTemplate: MongoTemplate) {

        // then
        assertThat(mongoTemplate.findAll(Document::class.java, "collection"))
                .extracting("key")
                .isEmpty()
    }
}
