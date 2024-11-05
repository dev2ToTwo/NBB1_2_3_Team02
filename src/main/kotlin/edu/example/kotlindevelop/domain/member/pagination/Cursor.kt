package edu.example.kotlindevelop.domain.member.pagination

import java.time.LocalDateTime

data class Cursor (
    val lastCreatedAt : LocalDateTime, // 마지막 조회 멤버의 생성 시간
    val lastId : Long // 마지막 조회 멤버의 ID
)