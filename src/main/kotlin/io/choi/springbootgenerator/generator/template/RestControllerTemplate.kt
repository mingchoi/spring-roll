package io.choi.springbootgenerator.generator.template

import io.choi.springbootgenerator.annotation.FindBy
import io.choi.springbootgenerator.generator.EntityInfo


class RestControllerTemplate(private val info: EntityInfo) {
    private val cClassName = info.className
    private val className = info.className.toLowerCase()
    private val packageName = info.packageName

    private val findByColumns = info.parameters.filter {
        it.annotation.any { an -> an is FindBy }
    }

    private val findByReqPara = findByColumns.joinToString(",\n\t\t\t\t") { "@RequestParam ${it.name}: ${it.type}" }

    private val findByWhenCase = findByColumns.joinToString("\n") {
        "                ${it.name} != null -> ResponseEntity(\n" +
                "                        ${info.className.toLowerCase()}Service?.findBy${it.name.capitalize()}(${it.name})!!\n" +
                "                                .map { it.sanitize() },\n" +
                "                        HttpStatus.OK\n" +
                "                )"
    }

    fun build() = """
package ${info.packageName}.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

import ${info.packageName}.service.${info.className}Service
import ${info.packageName}.dto.${info.className}Dto

@RestController
@RequestMapping("${info.className.toLowerCase()}")
class ${info.className}Controller {
    @Autowired
    var ${info.className.toLowerCase()}Service: ${info.className}Service? = null
    
    @PostMapping
    fun create(@RequestBody ${info.className.toLowerCase()}: ${info.className}Dto): ResponseEntity<${info.className}Dto> =
            try {
                val result = ${info.className.toLowerCase()}Service?.create(${info.className.toLowerCase()})
                ResponseEntity(result?.sanitize(), HttpStatus.CREATED)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<${info.className}Dto> =
            try {
                val result = ${info.className.toLowerCase()}Service?.findById(id)
                ResponseEntity(result?.sanitize(), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

    @GetMapping
    fun findAll(${findByReqPara}): ResponseEntity<List<${info.className}Dto>> =
            when {
$findByWhenCase
                else -> ResponseEntity(
                        ${info.className.toLowerCase()}Service?.findAll()!!
                                .map { it.sanitize() },
                        HttpStatus.OK
                )
            }
            
    @PutMapping
    fun update(@RequestBody ${info.className.toLowerCase()}: ${info.className}Dto): ResponseEntity<${info.className}Dto> =
            try {
                val result = ${info.className.toLowerCase()}Service?.update(${info.className.toLowerCase()})
                ResponseEntity(result?.sanitize(), HttpStatus.CREATED)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

    @DeleteMapping
    fun delete(@RequestBody ${info.className.toLowerCase()}: ${info.className}Dto): ResponseEntity<Unit> =
            try {
                val result = ${info.className.toLowerCase()}Service?.delete(${info.className.toLowerCase()})
                ResponseEntity(HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }
} 
  """.trimIndent()
}