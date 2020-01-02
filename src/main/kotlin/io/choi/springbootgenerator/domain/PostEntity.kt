package io.choi.springbootgenerator.domain

import io.choi.springbootgenerator.GenerateRestful
import javax.persistence.Entity
import javax.persistence.Id

@GenerateRestful
@Entity
class PostEntity(
        @Id
        var id: String? = null,
        var username: String? = null,
        var password: String? = null,
        var age: Int? = null,
        var vip: Boolean? = null
)