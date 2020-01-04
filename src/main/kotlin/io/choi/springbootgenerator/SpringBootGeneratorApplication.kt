package io.choi.springbootgenerator

import org.springframework.boot.autoconfigure.SpringBootApplication

import io.choi.springbootgenerator.domain.UserEntity
import io.choi.springbootgenerator.generator.Generator

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
