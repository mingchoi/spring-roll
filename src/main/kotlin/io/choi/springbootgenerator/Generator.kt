package io.choi.springbootgenerator

import io.choi.springbootgenerator.annotation.FindBy
import io.choi.springbootgenerator.annotation.Santized
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.full.primaryConstructor

data class ColumnInfo(
        val name: String,
        val type: KType,
        val annotation: Array<Annotation>
)

data class EntityInfo(
        val className: String,
        val parameters: List<ColumnInfo>,
        val packageName: String
)

class Generator {
    fun analyze(target: KClass<*>) = EntityInfo(
            className = target.simpleName!!.replace("Entity", ""),
            parameters = target.java.declaredFields.map { f ->
                ColumnInfo(
                        f.name,
                        target.primaryConstructor?.parameters?.findLast { it.name == f.name }!!.type,
                        f.annotations)
            },
            packageName = target.jvmName.split(".domain.")[0]
    )

    fun createAll(target: KClass<*>, overWrite: Boolean = false) {
        val info = analyze(target)
        createRepository(info, overWrite)
        createDto(info, overWrite)
        createService(info, overWrite)
        createRestController(info, overWrite)
    }

    fun createDto(info: EntityInfo, overWrite: Boolean = false) {
        val paras = info.parameters?.joinToString(",\n", transform = {
            "\tval ${it.name}: ${it.type.toString().replace("kotlin.", "")}"
        })
        val entityParasName = info.parameters?.map { it.name }

        val sanitizeColumns = info.parameters.filter {
            it.annotation.any { an -> an is Santized }
        }

        val sb = StringBuilder().apply {
            apply {
                append("""
package ${info.packageName}.dto

import com.fasterxml.jackson.annotation.JsonInclude

import ${info.packageName}.domain.${info.className}Entity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ${info.className}Dto (
${paras}
){
    fun toEntity() = ${info.className}Entity(${entityParasName.joinToString(", ")})
    
    fun sanitize() = this.copy(${sanitizeColumns.joinToString(", ") { it.name + " = null" }})
    
    companion object {
        fun fromEntity(e: ${info.className}Entity) = ${info.className}Dto(${entityParasName.joinToString(", ", transform = { "e.$it" })})
    }
}
            """.trimIndent())
            }
        }

        File(typePath(info, "Dto")).apply {
            when (exists()) {
                false -> {
                    parentFile.mkdirs()
                    writeText(sb.toString())
                }
                true -> {
                    if (overWrite) {
                        delete()
                        writeText(sb.toString())
                    }
                }
            }
        }
    }

    fun createRepository(info: EntityInfo, overWrite: Boolean = false) {
        val findByColumns = info.parameters.filter {
            it.annotation.any { an -> an is FindBy }
        }
        val methodList = findByColumns.joinToString("\n") {
            "\tfun findBy${it.name.capitalize()}(${it.name.toLowerCase()
            }: ${it.type.toString().replace("kotlin.", "").replace("?", "")
            }): List<${info.className}Entity>"
        }
        val sb = StringBuilder().apply {
            append("""
package ${info.packageName}.repository

import org.springframework.data.repository.CrudRepository
import ${info.packageName}.domain.${info.className}Entity

interface ${info.className}Repository : CrudRepository<${info.className}Entity, String>{
$methodList
}
            """.trimIndent())
        }

        File(typePath(info, "Repository")).apply {
            when (exists()) {
                false -> {
                    parentFile.mkdirs()
                    writeText(sb.toString())
                }
                true -> {
                    if (overWrite) {
                        delete()
                        writeText(sb.toString())
                    }
                }
            }
        }
    }

    fun createService(info: EntityInfo, overWrite: Boolean = true) {
        val idField = info.parameters
                .first { it.name.toLowerCase() == "id" }
        val interfaceFindByIdLine = "fun findById(${idField.name}:${idField.type.toString().replace("kotlin.", "").replace("?", "")}): ${info.className}Dto?"

        val findByColumns = info.parameters.filter {
            it.annotation.any { an -> an is FindBy }
        }
        val interfaceMethodList = findByColumns.joinToString("\n") {
            "\tfun findBy${it.name.capitalize()}(${it.name.toLowerCase()
            }: ${it.type.toString().replace("kotlin.", "").replace("?", "")
            }): List<${info.className}Dto>"
        }
        val implementMethodList = findByColumns.joinToString("\n") {
            "\toverride fun findBy${it.name.capitalize()}(${it.name.toLowerCase()
            }: ${it.type.toString().replace("kotlin.", "").replace("?", "")
            }): List<${info.className}Dto> = \n\t\t${
            info.className.toLowerCase()}Repository?.findBy${it.name.capitalize()}(${it.name.toLowerCase()
            })!!.map { ${info.className}Dto.fromEntity(it) }"
        }
        val sb = StringBuilder().apply {
            append("""
package ${info.packageName}.service               

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

import ${info.packageName}.dto.${info.className}Dto
import ${info.packageName}.repository.${info.className}Repository

interface ${info.className}Service {
    fun create(${info.className.toLowerCase()}: ${info.className}Dto): ${info.className}Dto?
    $interfaceFindByIdLine
    fun findAll(): List<${info.className}Dto>
$interfaceMethodList
    fun update(${info.className.toLowerCase()}: ${info.className}Dto): ${info.className}Dto?
    fun delete(${info.className.toLowerCase()}: ${info.className}Dto)
}

@Service
class ${info.className}ServiceImpl : ${info.className}Service {
    @Autowired
    var ${info.className.toLowerCase()}Repository: ${info.className}Repository? = null

    override fun create(${info.className.toLowerCase()}: ${info.className}Dto): ${info.className}Dto? =
        ${info.className}Dto.fromEntity(
            ${info.className.toLowerCase()}Repository?.save(
                ${info.className.toLowerCase()}.copy(
                    id = UUID.randomUUID().toString()
                ).toEntity()
            )!!
        )

    override fun findById(id: String): ${info.className}Dto? =
        ${info.className}Dto.fromEntity(
            ${info.className.toLowerCase()}Repository?.findById(id)!!.get()
        )
    
    override fun findAll(): List<${info.className}Dto> =
        ${info.className.toLowerCase()}Repository?.findAll()!!.map { ${info.className}Dto.fromEntity(it) }
        
$implementMethodList

    override fun update(${info.className.toLowerCase()}: ${info.className}Dto): ${info.className}Dto =
        ${info.className}Dto.fromEntity(
            ${info.className.toLowerCase()}Repository?.save(${info.className.toLowerCase()}.toEntity())!!
        )

    override fun delete(${info.className.toLowerCase()}: ${info.className}Dto): Unit =
            ${info.className.toLowerCase()}Repository?.delete(${info.className.toLowerCase()}.toEntity())!!
}
            """.trimIndent())
        }

        File(typePath(info, "Service")).apply {
            when (exists()) {
                false -> {
                    parentFile.mkdirs()
                    writeText(sb.toString())
                }
                true -> {
                    if (overWrite) {
                        delete()
                        writeText(sb.toString())
                    }
                }
            }
        }
    }

    fun createRestController(info: EntityInfo, overWrite: Boolean = false) {

        val findByColumns = info.parameters.filter {
            it.annotation.any { an -> an is FindBy }
        }

        val implementMethodList = findByColumns.joinToString("\n") {
            """
    fun findBy${it.name.capitalize()}(${it.name.toLowerCase()}: ${
            it.type.toString().replace("kotlin.", "").replace("?", "")
            }): ResponseEntity<List<${info.className}Dto>> =
            ResponseEntity(
                    ${info.className.toLowerCase()}Service?.findBy${it.name.capitalize()}(${it.name.toLowerCase()})!!
                            .map { it.sanitize() },
                    HttpStatus.OK
            )
            """
        }

        val sb = StringBuilder().apply {
            append("""
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
    fun findAll(): ResponseEntity<List<${info.className}Dto>> =
            ResponseEntity(
                    ${info.className.toLowerCase()}Service?.findAll()!!
                            .map { it.sanitize() },
                    HttpStatus.OK
            )
$implementMethodList
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
            """.trimIndent())
        }

        File(typePath(info, "Controller")).apply {
            when (exists()) {
                false -> {
                    parentFile.mkdirs()
                    writeText(sb.toString())
                }
                true -> {
                    if (overWrite) {
                        delete()
                        writeText(sb.toString())
                    }
                }
            }
        }
    }

    private fun typePath(info: EntityInfo, type: String) = "src/main/kotlin/${info.packageName.replace(".", "/")}" +
            "/${type.toLowerCase()}/${info.className}${type}.kt"
}