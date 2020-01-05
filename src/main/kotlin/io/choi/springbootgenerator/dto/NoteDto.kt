package io.choi.springbootgenerator.dto

import com.fasterxml.jackson.annotation.JsonInclude

import io.choi.springbootgenerator.domain.NoteEntity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NoteDto (
	val id: String?,
	val user: UserDto?,
	val title: String?,
	val content: String?
){
    fun toEntity() = NoteEntity(id, user?.toEntity(), title, content)
    
    fun sanitize() = this.copy(user = user?.sanitize())
    
    companion object {
        fun fromEntity(e: NoteEntity) = NoteDto(e.id, e.user?.let { UserDto.fromEntity(it) }, e.title, e.content)
    }
}