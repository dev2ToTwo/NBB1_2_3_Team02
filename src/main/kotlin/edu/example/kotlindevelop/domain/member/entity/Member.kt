package edu.example.kotlindevelop.domain.member.entity

import edu.example.kotlindevelop.domain.product.lossrate.entity.LossRate
import edu.example.kotlindevelop.domain.product.product.entity.Product
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Member(
    private val loginId: String,  // 생성자 매개변수에 private 추가
    private val pw: String,        // 생성자 매개변수에 private 추가
    var name: String,              // var로 선언
    private val mImage: String,    // 생성자 매개변수에 private 추가
    private val email: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(columnDefinition = "TEXT")
    private var refreshToken: String? = null

    @CreatedDate
    private var createdAt: LocalDateTime? = null

    @LastModifiedDate
    private var modifiedAt: LocalDateTime? = null

    @OneToMany(mappedBy = "maker", cascade = [CascadeType.ALL], orphanRemoval = true)
    var productList: MutableList<Product> = ArrayList()

    @OneToMany(mappedBy = "maker", cascade = [CascadeType.ALL], orphanRemoval = true)
    var lossRateList: MutableList<LossRate> = ArrayList()

//    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
//    var ordersList: List<Orders> = ArrayList()
}