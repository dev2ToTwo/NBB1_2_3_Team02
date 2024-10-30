package edu.example.kotlindevelop.domain.product.product.entity

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.lossrate.entity.LossRate
import jakarta.persistence.*

@Entity
@Table(name = "product")
class Product(
    val name: String,
    @ManyToOne // 관계 매핑
    @JoinColumn(name = "member_id") // 외래 키 설정
    var maker: Member? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    var lossRateList: MutableList<LossRate> = ArrayList()
}
