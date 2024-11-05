package edu.example.kotlindevelop.domain.member.entity

import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.product.entity.Product
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(unique = true)
    var loginId: String,
    var pw: String,
    var name: String,
    var email: String,
    var mImage: String? = null,
    var userName: String? = null,

    @Column(columnDefinition = "TEXT")
    var refreshToken: String? = null,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    val modifiedAt: LocalDateTime = LocalDateTime.now(),


    @OneToMany(mappedBy = "maker", cascade = [CascadeType.ALL], orphanRemoval = true)
    var productList: MutableList<Product> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    var ordersList: MutableList<Orders> = mutableListOf()
) {
    fun updateRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken

    }
}
