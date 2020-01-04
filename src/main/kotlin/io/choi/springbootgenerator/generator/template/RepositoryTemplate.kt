package io.choi.springbootgenerator.generator.template

import io.choi.springbootgenerator.annotation.FindBy
import io.choi.springbootgenerator.generator.EntityInfo

class RepositoryTemplate(private val info: EntityInfo) {
  private val className = info.className
  private val packageName = info.packageName

  private val findByColumns = info.parameters.filter {
    it.annotation.any { an -> an is FindBy }
  }

  private val methodList = findByColumns.joinToString("\n") {
    val cName = it.name.capitalize()
    val lName = it.name.toLowerCase()
    val inputType = it.type.toString()
        .replace("kotlin.", "")
        .replace("?", "")
    "\tfun findBy${cName}(${lName}: ${inputType}): List<${className}Entity>"
  }

  fun build() = """
package ${packageName}.repository

import org.springframework.data.repository.CrudRepository
import ${packageName}.domain.${className}Entity

interface ${className}Repository : CrudRepository<${className}Entity, String>{
$methodList
}
  """.trimIndent()
}
