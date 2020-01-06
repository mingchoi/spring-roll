package io.choi.springbootgenerator.security

import io.jsonwebtoken.Jwts
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class AuthorizationFilter(
        authManager: AuthenticationManager,
        private val env: Environment
) : BasicAuthenticationFilter(authManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest,
                                  res: HttpServletResponse,
                                  chain: FilterChain) {
        val authorizationHeader = req.getHeader(env?.getProperty("authorization.token.header.name"))
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith(env?.getProperty("authorization.token.header.prefix")!!)) {
            chain.doFilter(req, res)
            return
        }
        val authentication = getAuthentication(req)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, res)
    }

    private fun getAuthentication(req: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val authorizationHeader = req.getHeader(env?.getProperty("authorization.token.header.name"))
                ?: return null
        val token: String = authorizationHeader.replace(env.getProperty("authorization.token.header.prefix")!!, "")
        val userId: String = Jwts.parser()
                .setSigningKey(env?.getProperty("token.secret"))
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
                ?: return null
        return UsernamePasswordAuthenticationToken(userId, null, ArrayList())
    }

    init {
        this.environment = env
    }
}