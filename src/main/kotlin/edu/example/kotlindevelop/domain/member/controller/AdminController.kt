package edu.example.kotlindevelop.domain.member.controller

import edu.example.kotlindevelop.domain.member.dto.MemberDTO
import edu.example.kotlindevelop.domain.member.service.MemberService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/adm")
class AdminController (
    private val memberService: MemberService,
//    private val productService: ProductService
){


    //모든 회원 정보 조회하기
    @GetMapping("/members/all")
    fun getMembers(@RequestParam(defaultValue = "0") page: Int): ResponseEntity<Page<MemberDTO.Response>> {
        val pageSize = 10
        val pageable: Pageable = PageRequest.of(page, pageSize)
        val responseDto: Page<MemberDTO.Response> = memberService.readAll(pageable)

      return ResponseEntity.ok(responseDto)
    }

    //무한 스크롤 회원 정보 조회하기
    @GetMapping("/members/all/cursor")
    fun getMembersCursorBased(
        @RequestParam lastCreatedAt : LocalDateTime?,
        @RequestParam lastId : Long?,
        @RequestParam (defaultValue = "10") limit: Int,
    ): ResponseEntity<MemberDTO.MemberCursorResponse> {
        val response : MemberDTO.MemberCursorResponse = memberService.readAllCursorBased(
            lastCreatedAt,
            lastId,
            limit
        )

        return ResponseEntity.ok(response)
    }


    // 아래는 ProductService merge 후 완성

//    //모든 회원 식재료 정보 조회하기
//    @GetMapping("/products/all")
//    fun getProducts(pageRequestDTO: ProductDTO.PageRequestDTO?): ResponseEntity<Page<ProductDTO>> {
//        val responseDto: Page<ProductDTO> = productService.getProducts(pageRequestDTO)
//        return ResponseEntity.ok<Page<ProductDTO>>(responseDto)
//    }

//    // 상품 이름 검색
//    @GetMapping("/products/search")
//    fun searchProducts(
//        @RequestParam("keyword") keyword: String?,
//        pageRequestDTO: ProductDTO.PageRequestDTO?
//    ): ResponseEntity<Page<ProductDTO>> {
//        val productDTOPage: Page<ProductDTO> = productService.searchProducts(keyword, pageRequestDTO)
//        return ResponseEntity.ok<Page<ProductDTO>>(productDTOPage)
//    }
}