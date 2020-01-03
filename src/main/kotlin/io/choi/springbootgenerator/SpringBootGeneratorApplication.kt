package io.choi.springbootgenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import io.choi.springbootgenerator.domain.UserEntity

@SpringBootApplication
class SpringBootGeneratorApplication

fun main(args: Array<String>) {
    generateFromEntity()
//    runApplication<SpringBootGeneratorApplication>(*args)
}

fun generateFromEntity() =
        Generator().apply {
            createAll(UserEntity::class, true)
        }
