package edu.example.kotlindevelop.domain.product.product.repository

import edu.example.kotlindevelop.domain.product.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product?, Long?> {

}