package io.choi.springbootgenerator.repository

import org.springframework.data.repository.CrudRepository
import io.choi.springbootgenerator.domain.PostEntity

interface PostRepository : CrudRepository<PostEntity, String>{
}