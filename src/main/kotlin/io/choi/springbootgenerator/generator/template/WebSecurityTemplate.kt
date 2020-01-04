package io.choi.springbootgenerator.generator.template

import io.choi.springbootgenerator.generator.EntityInfo

class WebSecurityTemplate(private val info: EntityInfo) {
    private val packageName = info.packageName

    fun build() = """
package ${packageName}.security

import ${packageName}.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class WebSecurity : WebSecurityConfigurerAdapter() {
    @Autowired
    val env: Environment? = null

    @Autowired
    val userService: UserService? = null

    override fun configure(http: HttpSecurity?) {
        http!!.csrf().disable()
        http!!.authorizeRequests()
                .antMatchers("/**")
                .hasIpAddress(env?.getProperty("gateway.ip"))
                .and()
                .addFilter(buildAuthenticationFilter())
        http.headers().frameOptions().disable()
    }

    private fun buildAuthenticationFilter() =
            AuthenticationFilter(env!!, userService!!, authenticationManager())
                    .apply {
                        setFilterProcessesUrl(
                                env!!.getProperty("login.url.path")
                        )
                    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userService)
                ?.passwordEncoder(BCryptPasswordEncoder())
    }
}
    """.trimIndent()
}
