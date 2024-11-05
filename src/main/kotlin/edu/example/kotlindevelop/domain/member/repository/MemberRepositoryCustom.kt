package edu.example.kotlindevelop.domain.member.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface MemberRepositoryCustom {
    fun searchMembers(pageable: Pageable): Page<Member>
    fun searchMemberCursorBased(
        lastCreatedAt: LocalDateTime?,
        lastId:Long?,
        limit:Int
    ) : List<Member>
}