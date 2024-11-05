package edu.example.kotlindevelop.global.initData


import edu.example.kotlindevelop.domain.member.dto.MemberDTO
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.repository.MemberRepository
import edu.example.kotlindevelop.domain.member.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional


@Configuration
@Profile("!prod")
class NotProd(
    private val memberService: MemberService,
    private val memberRepository: MemberRepository

) {

    @Autowired
    @Lazy
    private lateinit var self: NotProd

    @Bean
    fun initNotProd(): ApplicationRunner = ApplicationRunner {
        self.work1()
    }

    @Transactional
    fun work1() {
        if (memberService.count() > 0) return

        // admin
        val adminRequest = MemberDTO.CreateRequestDto(
            loginId = "admin",
            pw = "1234",
            name = "운영자",
            email = "admin@gmail.com"
        )

        memberService.create(adminRequest)

        // 10만 회원 생성 _ dto
//        for (i in 1..100000) {
//            val createDto = MemberDTO.CreateRequestDto(
//                loginId = "abc$i",
//                pw = "1234",
//                name = "회원$i",
//                email = "abc$i@gmail.com"
//            )
//            memberService.create(createDto)
//        }

        // 10만 회원 생성 - bulk query
        val members = mutableListOf<Member>()
        for (i in 1..2) {
            members.add(Member(null ,"abc$i","1234","testname$i","test$i@naver.com" ))
        }
        memberService.bulkInsertMembers(members)

    }


}


