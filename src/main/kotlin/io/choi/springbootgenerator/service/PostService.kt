package io.choi.springbootgenerator.service

import io.choi.springbootgenerator.dto.PostDto
import io.choi.springbootgenerator.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired

interface PostService {
    fun createPost(post: PostDto): PostDto
    fun getPostById(id: String): PostDto
    fun updatePost(post: PostDto): PostDto
    fun deletePost(post: PostDto): Unit
}

class PostServiceImpl : PostService {
    @Autowired
    var postRepository: PostRepository? = null

    override fun createPost(post: PostDto): PostDto {
        return PostDto.fromEntity(
                postRepository?.save(post.toEntity())!!
        )
    }

    override fun getPostById(id: String): PostDto {
        return PostDto.fromEntity(
                postRepository?.findById(id)!!.get()
        )
    }

    override fun updatePost(post: PostDto): PostDto {
        return PostDto.fromEntity(
                postRepository?.save(post.toEntity())!!
        )
    }

    override fun deletePost(post: PostDto): Unit {
        postRepository?.delete(post.toEntity())
    }
}