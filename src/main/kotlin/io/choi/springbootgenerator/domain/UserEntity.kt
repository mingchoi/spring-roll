package io.choi.springbootgenerator.domain

import io.choi.springbootgenerator.annotation.FindBy
import io.choi.springbootgenerator.annotation.Santized
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user")
class UserEntity(
        @Id
        var id: String? = null,

        @FindBy
        @Column(nullable = false, length = 50, unique = true)
        var username: String? = null,

        @Santized
        @Column(nullable = false, length = 100)
        var password: String? = null,

        @FindBy
        @Column(nullable = false, length = 120, unique = true)
        @Santized
        var email: String? = null,

        @Column(nullable = false)
        var age: Int? = null,

        @Column(nullable = false)
        var vip: Boolean? = null
)