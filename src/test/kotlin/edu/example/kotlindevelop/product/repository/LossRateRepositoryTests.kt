package edu.example.kotlindevelop.product.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.repository.MemberRepository
import edu.example.kotlindevelop.domain.product.lossrate.entity.LossRate
import edu.example.kotlindevelop.domain.product.product.entity.Product
import edu.example.kotlindevelop.domain.product.lossrate.repository.LossRateRepository
import edu.example.kotlindevelop.domain.product.product.repository.ProductRepository

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
class LossRateRepositoryTests {
    @Autowired
    private val productRepository: ProductRepository? = null

    @Autowired
    private val memberRepository: MemberRepository? = null

    @Autowired
    private val lossRateRepository: LossRateRepository? = null

    @Test
    @Transactional
    fun 로스율등록테스트() {
        // Given - 회원 및 식재료 생성
        val member: Member = Member(
            loginId = "membertest",
            pw = "qwer",
            name = "테스트",
            mImage = "아바타",
            email = "example@example.com"
        )
        val savedMember = memberRepository!!.save(member)

        val product: Product = Product(
            name = "양파",
            maker = savedMember
        )
        val savedProduct = productRepository!!.save(product)
        savedMember.productList.add(product)

        // When - 로스율 생성 및 등록
        val lossRate: LossRate = LossRate(
            maker = savedMember,
            product = savedProduct,
            loss = 10,
        )
        val savedLossRate = lossRateRepository!!.save(lossRate)

        // Then - 검증
        println("member id: ${savedMember.id}")
        println("member name: ${savedMember.name}")
        println(savedMember.productList)

        println("product's member id: ${savedProduct.maker?.id}")
        println("loss rate for product: ${savedLossRate.loss}")

        assertNotNull(savedProduct) // 저장된 상품이 null이 아님을 확인
        assertEquals(savedMember.name, savedProduct.maker?.name) // 상품의 제작자 이름이 일치하는지 확인
        assertEquals("양파", savedProduct.name) // 상품명이 "양파"인지 확인
        assertEquals(10, savedLossRate.loss) // 저장된 로스율이 10인지 확인
    }
}