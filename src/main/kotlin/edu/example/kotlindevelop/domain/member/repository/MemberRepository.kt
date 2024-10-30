package edu.example.kotlindevelop.domain.member.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

}