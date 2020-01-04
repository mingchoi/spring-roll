package io.choi.springbootgenerator.generator.template

import io.choi.springbootgenerator.annotation.FindBy
import io.choi.springbootgenerator.generator.EntityInfo


class ServiceTemplate(private val info: EntityInfo) {
    private val cClassName = info.className
    private val className = info.className.toLowerCase()
    private val packageName = info.packageName

    private val idField = info.parameters
            .first { it.name.toLowerCase() == "id" }

    private val idFieldType = idField.type.toString().replace("kotlin.", "").replace("?", "")

    private val interfaceFindByIdLine = "fun findById(${idField.name}:$idFieldType): ${cClassName}Dto?"

    private val findByColumns = info.parameters.filter {
        it.annotation.any { an -> an is FindBy }
    }

    private val interfaceMethodList = findByColumns.joinToString("\n") {
        val Name = it.name.capitalize()
        val name = it.name.toLowerCase()
        val type = it.type.toString()
                .replace("kotlin.", "")
                .replace("?", "")
        "    fun findBy${Name}(${name}: ${type}): List<${cClassName}Dto>"
    }

    private val implementMethodList = findByColumns.joinToString("\n\n") {
        val Name = it.name.capitalize()
        val name = it.name.toLowerCase()
        val type = it.type.toString()
                .replace("kotlin.", "")
                .replace("?", "")
        "    override fun findBy${Name}(${name}: ${type}): List<${cClassName}Dto> = \n" +
                "        ${className}Repository?.findBy${Name}(${name})!!.map { ${cClassName}Dto.fromEntity(it) }"
    }

    private val isUser = className == "user"

    fun build() = """
package ${packageName}.service               

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

import ${packageName}.dto.${cClassName}Dto
import ${packageName}.repository.${cClassName}Repository

interface ${cClassName}Service${if (isUser) ": UserDetailsService" else ""} {
    fun create(${className}: ${cClassName}Dto): ${cClassName}Dto?
    $interfaceFindByIdLine
    fun findAll(): List<${cClassName}Dto>
$interfaceMethodList
    fun update(${className}: ${cClassName}Dto): ${cClassName}Dto?
    fun delete(${className}: ${cClassName}Dto)
}

@Service
class ${cClassName}ServiceImpl : ${cClassName}Service {
    @Autowired
    var ${className}Repository: ${cClassName}Repository? = null

    override fun create(${className}: ${cClassName}Dto): ${cClassName}Dto? =
        ${cClassName}Dto.fromEntity(
            ${className}Repository?.save(
                ${className}.copy(
                    id = UUID.randomUUID().toString()${if (isUser) "\n,password = BCryptPasswordEncoder().encode(user.password)" else ""}
                ).toEntity()
            )!!
        )

    override fun findById(id: String): ${cClassName}Dto? =
        ${cClassName}Dto.fromEntity(
            ${className}Repository?.findById(id)!!.get()
        )
    
    override fun findAll(): List<${cClassName}Dto> =
        ${className}Repository?.findAll()!!.map { ${cClassName}Dto.fromEntity(it) }
        
$implementMethodList

    override fun update(${className}: ${cClassName}Dto): ${cClassName}Dto =
        ${cClassName}Dto.fromEntity(
            ${className}Repository?.save(${className}.toEntity())!!
        )

    override fun delete(${className}: ${cClassName}Dto): Unit =
            ${className}Repository?.delete(${className}.toEntity())!!
${if (isUser) """
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException(username)
        } else {
            val u = userRepository?.findByUsername(username)?.first()
                    ?: throw UsernameNotFoundException(username)
            return User(u.username,
                    u.password,
                    true,
                    true,
                    true,
                    true,
                    emptyList())
        }
    } 
""".trimIndent() else ""}
}
  """.trimIndent()
}
