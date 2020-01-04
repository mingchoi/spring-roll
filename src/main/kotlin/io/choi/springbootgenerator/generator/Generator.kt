package io.choi.springbootgenerator.generator

import io.choi.springbootgenerator.generator.template.DtoTemplate
import io.choi.springbootgenerator.generator.template.RepositoryTemplate
import io.choi.springbootgenerator.generator.template.RestControllerTemplate
import io.choi.springbootgenerator.generator.template.ServiceTemplate
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

  fun createDto(info: EntityInfo, overWrite: Boolean = false) =
      save(
          typePath(info, "Dto"),
          DtoTemplate(info).build(),
          overWrite
      )


  fun createRepository(info: EntityInfo, overWrite: Boolean = false) =
      save(
          typePath(info, "Repository"),
          RepositoryTemplate(info).build(),
          overWrite
      )

  fun createService(info: EntityInfo, overWrite: Boolean = true) =
      save(
          typePath(info, "Service"),
          ServiceTemplate(info).build(),
          overWrite
      )

  fun createRestController(info: EntityInfo, overWrite: Boolean = false) =
      save(
          typePath(info, "Controller"),
          RestControllerTemplate(info).build(),
          overWrite
      )

  private fun save(path: String, content: String, overWrite: Boolean) = File(path).apply {
    when (exists()) {
      false -> {
        parentFile.mkdirs()
        writeText(content)
      }
      true -> {
        if (overWrite) {
          delete()
          writeText(content)
        }
      }
    }
  }

  private fun typePath(info: EntityInfo, type: String) = "src/main/kotlin/${info.packageName.replace(".", "/")}" +
      "/${type.toLowerCase()}/${info.className}${type}.kt"
}