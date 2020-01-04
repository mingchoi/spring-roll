package io.choi.springbootgenerator.generator.template

import io.choi.springbootgenerator.generator.EntityInfo

class AuthenticationFilterTemplate(private val info: EntityInfo) {
    private val packageName = info.packageName

    fun build() = """
package ${packageName}.security

import ${packageName}.dto.UserDto
import ${packageName}.service.UserService

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

data class LoginReqModel(val username: String, val password: String)

data class LoginResModel(val user: UserDto, val token: String)

class AuthenticationFilter(
        private val env: Environment,
        private val userService: UserService,
        authenticationManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter() {
    init {
        super.setAuthenticationManager(authenticationManager)
    }

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        try {
            val creds = jacksonObjectMapper().readValue<LoginReqModel>(
                    request?.inputStream,
                    LoginReqModel::class.java)
            return authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            creds.username,
                            creds.password,
                            emptyList()
                    )
            )
        } catch (e: Exception) {
            throw UsernameNotFoundException("")
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        val user = authResult?.principal
        if (user is User) {
            val u = userService.findByUsername(user.username).first()
            if (u != null) {
                val token = Jwts.builder()
                        .setSubject(u.id)
                        .setExpiration(Date(System.currentTimeMillis() +
                                env.getProperty("token.expiration_time")?.toLong()!!))
                        .signWith(SignatureAlgorithm.HS512,
                                env.getProperty("token.secret"))
                        .compact()
                val res = LoginResModel(u.sanitize(), token)
                response?.apply {
                    contentType = "application/json"
                    characterEncoding = "UTF-8"
                    writer.apply {
                        print(ObjectMapper().writeValueAsString(res))
                        flush()
                    }
                }
            }
        }
    }
}
    """.trimIndent()
}