package io.choi.springbootgenerator.dto

import com.fasterxml.jackson.annotation.JsonInclude

import io.choi.springbootgenerator.domain.UserEntity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto (
	val id: String?,
	val username: String?,
	val password: String?,
	val email: String?,
	val age: Int?,
	val vip: Boolean?
){
    fun toEntity() = UserEntity(id, username, password, email, age, vip)
    
    fun sanitize() = this.copy(password = null, email = null)
    
    companion object {
        fun fromEntity(e: UserEntity) = UserDto(e.id, e.username, e.password, e.email, e.age, e.vip)
    }
}