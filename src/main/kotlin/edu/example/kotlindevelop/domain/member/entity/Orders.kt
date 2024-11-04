package edu.example.kotlindevelop.domain.member.entity

import jakarta.persistence.*

@Entity
data class Orders(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne // Member와 관계를 설정
    @JoinColumn(name = "member_id") // 외래키 설정
    var member: Member? = null
)