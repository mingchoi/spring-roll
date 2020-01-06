package io.choi.springbootgenerator.generator.template

import io.choi.springbootgenerator.annotation.Santized
import io.choi.springbootgenerator.generator.EntityInfo


class DtoTemplate(private val info: EntityInfo) {
    private val className = info.className
    private val packageName = info.packageName

    private val paras = info.parameters
            .joinToString(",\n", transform = {
                val type = it.type.toString().let { t ->
                    if (t.contains("Entity?"))
                        t.replace("$packageName.domain.", "")
                                .replace("Entity?", "Dto?")
                    else
                        t.replace("kotlin.", "")
                }
                "\tval ${it.name}: $type = null"
            })

    private fun toEntityParasName() = info.parameters
            .joinToString(", ") {
                val type = it.type.toString()
                if (type.contains("Entity?")) "${it.name}?.toEntity()"
                else it.name
            }

    private fun fromEntityParasName() = info.parameters
            .joinToString(", ") {
                val type = it.type.toString()
                if (type.contains("Entity?"))
                    "e.${it.name}?.let { UserDto.fromEntity(it) }"
                else "e.${it.name}"
            }

    private val sanitizeColumns = info.parameters.filter {
        it.annotation.any { an -> an is Santized } ||
                it.type.toString().contains("Entity?")
    }.joinToString(", ") {
        if (it.type.toString().contains("Entity"))
            it.name + " = ${it.name}?.sanitize()"
        else
            it.name + " = null"
    }

    fun build() = """
package ${packageName}.dto

import com.fasterxml.jackson.annotation.JsonInclude

import ${packageName}.domain.${className}Entity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ${className}Dto (
$paras
){
    fun toEntity() = ${className}Entity(${toEntityParasName()})
    
    fun sanitize() = this.copy(${sanitizeColumns})
    
    companion object {
        fun fromEntity(e: ${className}Entity) = ${className}Dto(${fromEntityParasName()})
    }
}
  """.trimIndent()
}
