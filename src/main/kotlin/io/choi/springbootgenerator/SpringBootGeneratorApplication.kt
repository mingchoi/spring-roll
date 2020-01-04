package io.choi.springbootgenerator

import org.springframework.boot.autoconfigure.SpringBootApplication

import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootGeneratorApplication

fun main(args: Array<String>) {
    runApplication<SpringBootGeneratorApplication>(*args)
}

