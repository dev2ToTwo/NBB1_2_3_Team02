package edu.example.kotlindevelop.domain.member.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.querydsl.jpa.impl.JPAQueryFactory
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.entity.QMember
import java.time.LocalDateTime


@Repository
class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    // offeset 기반  페이징 처리
    override fun searchMembers(pageable: Pageable): Page<Member> {
        val member = QMember.member

        val content = queryFactory
            .selectFrom(member)
            .orderBy(member.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .selectFrom(member)
            .fetch()
            .size.toLong()

        return PageImpl(content, pageable, total)
    }


    //커서 기반 페이징 처리
    override fun searchMemberCursorBased(lastCreatedAt: LocalDateTime?, lastId: Long?, limit: Int): List<Member> {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(
                if (lastCreatedAt != null && lastId != null) {
                    member.createdAt.lt(lastCreatedAt)
                        .or(member.createdAt.eq(lastCreatedAt).and(member.id.lt(lastId)))
                } else {
                    null // 커서가 없으면 조건 없이 최신 데이터부터 조회
                }
            )
            .orderBy(member.createdAt.desc(), member.id.desc())
            .limit(limit.toLong() )
            .fetch()

    }


}
