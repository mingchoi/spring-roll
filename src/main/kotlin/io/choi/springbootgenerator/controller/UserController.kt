package io.choi.springbootgenerator.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

import io.choi.springbootgenerator.service.UserService
import io.choi.springbootgenerator.dto.UserDto

@RestController
@RequestMapping("user")
class UserController {
    @Autowired
    var userService: UserService? = null
    
    @PostMapping
    fun create(@RequestBody user: UserDto): ResponseEntity<UserDto> =
            try {
                val result = userService?.create(user)
                ResponseEntity(result?.sanitize(), HttpStatus.CREATED)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<UserDto> =
            try {
                val result = userService?.findById(id)
                ResponseEntity(result?.sanitize(), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

    @GetMapping
    fun findAll(@RequestParam username: kotlin.String?, 
@RequestParam email: kotlin.String?): ResponseEntity<List<UserDto>> =
            when {
                username != null -> ResponseEntity(
                        userService?.findByUsername(username)!!
                                .map { it.sanitize() },
                        HttpStatus.OK
                )
                email != null -> ResponseEntity(
                        userService?.findByEmail(email)!!
                                .map { it.sanitize() },
                        HttpStatus.OK
                )
                else -> ResponseEntity(
                        userService?.findAll()!!
                                .map { it.sanitize() },
                        HttpStatus.OK
                )
            }
            
    @PutMapping
    fun update(@RequestBody user: UserDto): ResponseEntity<UserDto> =
            try {
                val result = userService?.update(user)
                ResponseEntity(result?.sanitize(), HttpStatus.CREATED)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

    @DeleteMapping
    fun delete(@RequestBody user: UserDto): ResponseEntity<Unit> =
            try {
                val result = userService?.delete(user)
                ResponseEntity(HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }
}