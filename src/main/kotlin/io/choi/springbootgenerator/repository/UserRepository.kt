package io.choi.springbootgenerator.repository

import org.springframework.data.repository.CrudRepository
import io.choi.springbootgenerator.domain.UserEntity

interface UserRepository : CrudRepository<UserEntity, String>{
	fun findByUsername(username: String): List<UserEntity>
}