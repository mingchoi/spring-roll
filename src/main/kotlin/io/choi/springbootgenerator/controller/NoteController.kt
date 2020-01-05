package io.choi.springbootgenerator.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

import io.choi.springbootgenerator.service.NoteService
import io.choi.springbootgenerator.dto.NoteDto

@RestController
@RequestMapping("note")
class NoteController {
    @Autowired
    var noteService: NoteService? = null
    
    @PostMapping
    fun create(@RequestBody note: NoteDto): ResponseEntity<NoteDto> =
            try {
                val result = noteService?.create(note)
                ResponseEntity(result?.sanitize(), HttpStatus.CREATED)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<NoteDto> =
            try {
                val result = noteService?.findById(id)
                ResponseEntity(result?.sanitize(), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

    @GetMapping
    fun findAll(): ResponseEntity<List<NoteDto>> =
            when {

                else -> ResponseEntity(
                        noteService?.findAll()!!
                                .map { it.sanitize() },
                        HttpStatus.OK
                )
            }
            
    @PutMapping
    fun update(@RequestBody note: NoteDto): ResponseEntity<NoteDto> =
            try {
                val result = noteService?.update(note)
                ResponseEntity(result?.sanitize(), HttpStatus.CREATED)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

    @DeleteMapping
    fun delete(@RequestBody note: NoteDto): ResponseEntity<Unit> =
            try {
                val result = noteService?.delete(note)
                ResponseEntity(HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }
} 