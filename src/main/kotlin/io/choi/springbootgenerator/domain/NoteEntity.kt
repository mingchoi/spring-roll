package io.choi.springbootgenerator.domain

import javax.persistence.*

@Entity
@Table(name = "note")
class NoteEntity(
        @Id
        var id: String? = null,

        @ManyToOne
        var user: UserEntity? = null,

        @Column(nullable = false, length = 120)
        var title: String? = null,

        @Column(nullable = false, length = 512)
        var content: String? = null
)