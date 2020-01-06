package io.choi.springbootgenerator.service               

import java.util.UUID

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder 

import org.springframework.stereotype.Service

import io.choi.springbootgenerator.dto.UserDto

import io.choi.springbootgenerator.repository.UserRepository

interface UserService: UserDetailsService {
    fun create(user: UserDto): UserDto?
    fun findById(id:String): UserDto?
    fun findAll(): List<UserDto>
    fun findByUsername(username: String): List<UserDto>
    fun findByEmail(email: String): List<UserDto>
    fun update(user: UserDto): UserDto?
    fun delete(user: UserDto)
}

@Service
class UserServiceImpl : UserService {
    @Autowired
    var userRepository: UserRepository? = null

    override fun create(user: UserDto): UserDto? { 
        return UserDto.fromEntity(
            userRepository?.save(
                user.copy(
                    id = UUID.randomUUID().toString(),
                    password = BCryptPasswordEncoder().encode(user.password)
                ).toEntity()
            )!!
        )
    }

    override fun findById(id: String): UserDto? =
        UserDto.fromEntity(
            userRepository?.findById(id)!!.get()
        )
    
    override fun findAll(): List<UserDto> =
        userRepository?.findAll()!!.map { UserDto.fromEntity(it) }
        
    override fun findByUsername(username: String): List<UserDto> = 
        userRepository?.findByUsername(username)!!.map { UserDto.fromEntity(it) }

    override fun findByEmail(email: String): List<UserDto> = 
        userRepository?.findByEmail(email)!!.map { UserDto.fromEntity(it) }

    override fun update(user: UserDto): UserDto =
        UserDto.fromEntity(
            userRepository?.save(user.toEntity())!!
        )

    override fun delete(user: UserDto): Unit =
            userRepository?.delete(user.toEntity())!!

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException(username)
        } else {
            val result = userRepository?.findByUsername(username)!!
            if (result.isEmpty()) throw UsernameNotFoundException(username)
            val user = result.first()
            return User(user.username,
                    user.password,
                    true,
                    true,
                    true,
                    true,
                    emptyList())
        }
    }
}