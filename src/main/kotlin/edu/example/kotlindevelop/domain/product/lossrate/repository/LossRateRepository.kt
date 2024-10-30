package edu.example.kotlindevelop.domain.product.lossrate.repository

import edu.example.kotlindevelop.domain.product.lossrate.entity.LossRate
import org.springframework.data.jpa.repository.JpaRepository

interface LossRateRepository : JpaRepository<LossRate, Long> {

}