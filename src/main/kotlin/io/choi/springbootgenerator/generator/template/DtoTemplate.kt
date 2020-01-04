package io.choi.springbootgenerator.generator.template

import io.choi.springbootgenerator.annotation.Santized
import io.choi.springbootgenerator.generator.EntityInfo


class DtoTemplate(private val info: EntityInfo) {
  private val className = info.className
  private val packageName = info.packageName

  private val paras = info.parameters
      .joinToString(",\n", transform = {
        val type = it.type.toString().replace("kotlin.", "")
        "\tval ${it.name}: $type"
      })

  private fun entityParasName(isEntity: Boolean = false) = info.parameters
      .map { it.name }
      .joinToString(", ") { if (isEntity) "e.$it" else it }

  private val sanitizeColumns = info.parameters.filter {
    it.annotation.any { an -> an is Santized }
  }.joinToString(", ") { it.name + " = null" }

  fun build() = """
package ${packageName}.dto

import com.fasterxml.jackson.annotation.JsonInclude

import ${packageName}.domain.${className}Entity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ${className}Dto (
$paras
){
    fun toEntity() = ${className}Entity(${entityParasName()})
    
    fun sanitize() = this.copy(${sanitizeColumns})
    
    companion object {
        fun fromEntity(e: ${className}Entity) = ${className}Dto(${entityParasName(true)})
    }
}
  """.trimIndent()
}
