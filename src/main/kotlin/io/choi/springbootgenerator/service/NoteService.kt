package io.choi.springbootgenerator.service               

import java.util.UUID

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.stereotype.Service

import io.choi.springbootgenerator.dto.NoteDto
import io.choi.springbootgenerator.repository.NoteRepository

interface NoteService {
    fun create(note: NoteDto): NoteDto?
    fun findById(id:String): NoteDto?
    fun findAll(): List<NoteDto>

    fun update(note: NoteDto): NoteDto?
    fun delete(note: NoteDto)
}

@Service
class NoteServiceImpl : NoteService {
    @Autowired
    var noteRepository: NoteRepository? = null

    override fun create(note: NoteDto): NoteDto? =
        NoteDto.fromEntity(
            noteRepository?.save(
                note.copy(
                    id = UUID.randomUUID().toString()
                ).toEntity()
            )!!
        )

    override fun findById(id: String): NoteDto? =
        NoteDto.fromEntity(
            noteRepository?.findById(id)!!.get()
        )
    
    override fun findAll(): List<NoteDto> =
        noteRepository?.findAll()!!.map { NoteDto.fromEntity(it) }
        


    override fun update(note: NoteDto): NoteDto =
        NoteDto.fromEntity(
            noteRepository?.save(note.toEntity())!!
        )

    override fun delete(note: NoteDto): Unit =
            noteRepository?.delete(note.toEntity())!!

}