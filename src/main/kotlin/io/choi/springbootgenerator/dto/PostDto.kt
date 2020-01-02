package io.choi.springbootgenerator.dto

import io.choi.springbootgenerator.domain.PostEntity

data class PostDto(
        val id: String?,
        val username: String?,
        val password: String?,
        val age: Int?,
        val vip: Boolean?
) {
    fun toEntity() = PostEntity(id, username, password, age, vip)

    companion object {
        fun fromEntity(e: PostEntity) = PostDto(e.id, e.username, e.password, e.age, e.vip)
    }
}