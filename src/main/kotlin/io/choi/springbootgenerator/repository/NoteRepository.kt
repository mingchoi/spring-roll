package io.choi.springbootgenerator.repository

import org.springframework.data.repository.CrudRepository
import io.choi.springbootgenerator.domain.NoteEntity

interface NoteRepository : CrudRepository<NoteEntity, String>{

}