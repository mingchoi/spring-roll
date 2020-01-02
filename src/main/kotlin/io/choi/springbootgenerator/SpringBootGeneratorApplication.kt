package io.choi.springbootgenerator

import io.choi.springbootgenerator.domain.PostEntity
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SpringBootGeneratorApplication

fun main(args: Array<String>) {
    Generator().apply {
        createAll(PostEntity::class)
    }
//    runApplication<SpringBootGeneratorApplication>(*args)
}
