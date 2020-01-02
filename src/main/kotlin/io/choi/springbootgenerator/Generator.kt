package io.choi.springbootgenerator

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.full.primaryConstructor

data class EntityInfo(
        var className: String,
        var parameters: List<Pair<String?, KType>>,
        var packageName: String
)

class Generator {
    fun analyze(target: KClass<*>) = EntityInfo(
            className = target.simpleName!!.replace("Entity", ""),
            parameters = target.primaryConstructor?.parameters?.map { Pair(it.name, it.type) } ?: emptyList(),
            packageName = target.jvmName.split(".domain.")[0]
    )

    fun createAll(target: KClass<*>) {
        val info = analyze(target)
        createRepository(info)
        createDto(info)
        createService(info)
    }

    fun createDto(info: EntityInfo) {
        val sb = StringBuilder().apply {
            append("package ${info.packageName}.dto\n\n")
            append("data class ${info.className}Dto (\n")
            append(info.parameters?.joinToString(",\n", transform = {
                "\tval ${it.first}: ${it.second.toString().replace("kotlin.", "")}"
            }))
            append("\n)")
        }

        File(typePath(info, "Dto")).apply {
            if (!exists()) {
                parentFile.mkdirs()
                writeText(sb.toString())
            }
        }
    }

    fun createRepository(info: EntityInfo) {
        val sb = StringBuilder().apply {
            append("package ${info.packageName}.repository\n\n")
            append("import org.springframework.data.repository.CrudRepository\n")
            append("import ${info.packageName}.domain.${info.className}Entity\n\n")
            append("interface ${info.className}Repository : CrudRepository<${info.className}Entity, String>{\n")
            append("}")
        }

        File(typePath(info, "Repository")).apply {
            if (!exists()) {
                parentFile.mkdirs()
                writeText(sb.toString())
            }
        }
    }

    fun createService(info: EntityInfo) {
        val sb = StringBuilder().apply {
            append("package ${info.packageName}.service\n\n")
            append("import ${info.packageName}.dto.${info.className}Dto\n\n")
            append("interface ${info.className}Service {\n")
            append("\tfun create${info.className}(${info.className.toLowerCase()}: ${info.className}Dto): ${info.className}Dto\n")
            info.parameters
                    .first { it.first!!.toLowerCase() == "id" }
                    .apply {
                        append("\tfun get${info.className}ById(${first}:${second.toString().replace("kotlin.", "")}): ${info.className}Dto\n")
                    }
            append("\tfun update${info.className}(${info.className.toLowerCase()}: ${info.className}Dto): ${info.className}Dto\n")
            append("\tfun delete${info.className}(${info.className.toLowerCase()}: ${info.className}Dto): ${info.className}Dto\n")
            append("}\n\n")
        }

        File(typePath(info, "Service")).apply {
            if (!exists()) {
                parentFile.mkdirs()
                writeText(sb.toString())
            }
        }
    }

    fun createRestController(info: EntityInfo) {}

    private fun typePath(info: EntityInfo, type: String) = "src/main/kotlin/${info.packageName.replace(".", "/")}" +
            "/${type.toLowerCase()}/${info.className}${type}.kt"
}