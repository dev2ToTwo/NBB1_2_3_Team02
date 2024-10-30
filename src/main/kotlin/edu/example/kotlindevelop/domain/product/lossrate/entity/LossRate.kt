package edu.example.kotlindevelop.domain.product.lossrate.entity

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.product.entity.Product
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime


@Entity
@Table(name = "loss_rate")
@EntityListeners(AuditingEntityListener::class)
class LossRate(
    @ManyToOne
    @JoinColumn(name = "member_id")
    var maker: Member,  // 생성자에서 받는 maker

    @ManyToOne
    @JoinColumn(name = "product_id")
    var product: Product,  // 생성자에서 받는 product

    @Column(nullable = false)  // NULL을 허용하지 않도록 설정
    var loss: Int  // 생성자에서 받는 loss
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null  // id는 기본적으로 public입니다.

    @CreatedDate
    @Column(name = "recorded_at", nullable = false, updatable = false)
    var recordedAt: LocalDateTime? = null  // recordedAt 필드는 NULL을 허용
}
