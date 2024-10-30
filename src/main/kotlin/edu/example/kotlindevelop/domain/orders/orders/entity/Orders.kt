package edu.example.kotlindevelop.domain.orders.orders.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
class Orders {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null


    @CreatedDate @Column(name = "created_at", updatable = false) // 생성일은 업데이트하지 않음
    private var createdAt: LocalDateTime? = null

    @LastModifiedDate @Column(name = "modified_at") // 수정일
    private var modifiedAt: LocalDateTime? = null

}