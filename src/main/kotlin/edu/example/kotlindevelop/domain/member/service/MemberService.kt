package edu.example.kotlindevelop.domain.member.service


import edu.example.kotlindevelop.domain.member.dto.MemberDTO
import edu.example.kotlindevelop.domain.member.dto.MemberDTO.CustomOAuth2User
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.exception.MemberException
import edu.example.kotlindevelop.domain.member.pagination.Cursor
import edu.example.kotlindevelop.domain.member.repository.MemberRepository
import edu.example.kotlindevelop.domain.member.util.PasswordUtil
import edu.example.kotlindevelop.global.jwt.JwtUtil
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    @PersistenceContext private val entityManager: EntityManager
) {

    @Transactional
    fun create(dto: MemberDTO.CreateRequestDto): MemberDTO.StringResponseDto {
        // 기존의 회원이 있는지 검사
        val member: Optional<Member> = memberRepository.findByLoginId(dto.loginId)
        if (member.isPresent) {
            throw MemberException.MEMBER_ALREADY_EXISTS.memberTaskException
        }

        // 비밀번호 암호화
        dto.pw = passwordEncoder.encode(dto.pw)

        // 회원 저장
        memberRepository.save(dto.toEntity())

        return MemberDTO.StringResponseDto("회원가입이 완료되었습니다")
    }


    @Transactional
    fun update(dto: MemberDTO.Update): MemberDTO.Update {
        val memberOptional: Optional<Member> = memberRepository.findById(dto.id)

        if (memberOptional.isPresent) {
            val member = memberOptional.get().apply {
                loginId = dto.loginId ?: loginId
                pw = passwordEncoder.encode(dto.pw ?: pw)
                name = dto.name ?: name
                mImage = dto.mImage ?: mImage
                email = dto.email ?: email
            }
            memberRepository.save(member)

            return MemberDTO.Update(
                dto.id,
                member.loginId,
                member.pw,
                member.name,
                member.mImage,
                member.email
            )
        } else {
            throw MemberException.MEMBER_NOT_MODIFIED.memberTaskException
        }
    }

    @Transactional
    fun delete(id: Long) {
        val memberOptional: Optional<Member> = memberRepository.findById(id)
        if (memberOptional.isPresent) {
            memberRepository.delete(memberOptional.get())
        } else {
            throw MemberException.MEMBER_NOT_REMOVED.memberTaskException
        }
    }

    fun read(id: Long): MemberDTO.Response {
        val memberOptional: Optional<Member> = memberRepository.findById(id)
        if (memberOptional.isPresent) {
            return MemberDTO.Response(memberOptional.get())
        } else {
            throw MemberException.MEMBER_NOT_REMOVED.memberTaskException
        }
    }

    fun readAll(pageable: Pageable): Page<MemberDTO.Response> {
        val members: Page<Member> = memberRepository.searchMembers(pageable)
        return members.map { MemberDTO.Response(it) }
    }

    fun readAllCursorBased(
        lastCreatedAt: LocalDateTime?,
        lastId: Long?,
        limit: Int
    ): MemberDTO.MemberCursorResponse {
        val members: List<Member> = memberRepository.searchMemberCursorBased(lastCreatedAt, lastId, limit+1)

        val hasNext = members.size > limit
        val pagedMembers = if (hasNext) members.subList(0, limit).map { MemberDTO.Response(it) } else members.map {MemberDTO.Response(it) }


        val nextCursor: Cursor? = if (hasNext) {
            val lastMember = pagedMembers.last()
            println("lastMember$lastMember")
            Cursor(
                lastCreatedAt = lastMember.createdAt,
                lastId = lastMember.id!!
            )

        } else {
            null
        }

        println("Next Cursor: $nextCursor")


        return MemberDTO.MemberCursorResponse(
            members = pagedMembers,
            nextCursor = nextCursor
        )


    }


    private val uploadDir = "upload/" // 현재 디렉토리의 upload 폴더

    @Transactional
    fun changeImage(dto: MemberDTO.ChangeImage, imageFile: MultipartFile): MemberDTO.ChangeImageResponse { // MultipartFile 추가
        val memberOptional: Optional<Member> = memberRepository.findById(dto.id)
        if (memberOptional.isPresent) {
            val member: Member = memberOptional.get()
            val fileName = saveImage(imageFile)

            member.mImage = "api/v1/members/upload/${fileName}"
            val result = memberRepository.save(member)

            return MemberDTO.ChangeImageResponse(dto.id, result.mImage!!)
        } else {
            throw MemberException.MEMBER_IMAGE_NOT_MODIFIED.memberTaskException
        }
    }

    private fun saveImage(imageFile: MultipartFile): String { // MultipartFile로 변경
        try {
            val uploadPath = Paths.get(uploadDir)
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath)
            }

            // 파일 이름 생성 및 저장
            val fileName = System.currentTimeMillis().toString() + "_" + imageFile.originalFilename
            val filePath = uploadPath.resolve(fileName)
            Files.copy(imageFile.inputStream, filePath) // 실제 파일 저장

            return fileName // 저장된 파일 이름 반환
        } catch (e: IOException) {
            throw RuntimeException("파일 저장 실패", e)
        }
    }

    fun checkLoginIdAndPassword(loginId: String, pw: String): MemberDTO.LoginResponseDto {
        val opMember: Optional<Member> = memberRepository.findByLoginId(loginId)

        if (opMember.isEmpty) {
            throw MemberException.MEMBER_NOT_FOUND.memberTaskException
        }

        if (!passwordEncoder.matches(pw, opMember.get().pw)) {
            throw MemberException.MEMBER_LOGIN_DENIED.memberTaskException
        }

        return MemberDTO.LoginResponseDto(opMember.get())
    }

    fun getMemberById(id: Long): Member {
        val opMember: Optional<Member> = memberRepository.findById(id)

        if (opMember.isEmpty) {
            throw MemberException.MEMBER_NOT_FOUND.memberTaskException
        }

        return opMember.get()
    }

    fun count(): Int {
        return memberRepository.count().toInt()
    }

    @Transactional
    fun setRefreshToken(id: Long, refreshToken: String?) {
        val member: Member = memberRepository.findById(id).get()
        member.updateRefreshToken(refreshToken)
    }

    fun generateAccessToken(id: Long, loginId: String): String {
        val authorities = if (loginId == "admin") {
            listOf("ROLE_ADMIN")
        } else {
            listOf("ROLE_MEMBER")
        }

        return jwtUtil.encodeAccessToken(
            5,
            mapOf(
                "id" to id.toString(),
                "loginId" to loginId,
                "authorities" to authorities
            )
        )
    }

    fun generateRefreshToken(id: Long, loginId: String): String {
        return jwtUtil.encodeRefreshToken(
            60,
            mapOf(
                "id" to id.toString(),
                "loginId" to loginId
            )
        )
    }

    fun refreshAccessToken(refreshToken: String): String {
        //화이트리스트 처리
        val member: Member = memberRepository.findByRefreshToken(refreshToken)
            .orElseThrow { MemberException.MEMBER_LOGIN_DENIED.memberTaskException }

        //리프레시 토큰이 만료되었다면 로그아웃
        try {
            val claims: Claims = jwtUtil.decode(refreshToken) // 여기서 에러 처리가 남
        } catch (e: ExpiredJwtException) {
            // 클라이언트한테 만료되었다고 알려주기
            throw MemberException.MEMBER_REFRESHTOKEN_EXPIRED.memberTaskException
        }

        // member.id가 null인지 확인
        val memberId: Long = member.id ?: throw MemberException.MEMBER_LOGIN_DENIED.memberTaskException


        return generateAccessToken(memberId, member.loginId)
    }


    fun findByEmail(email: String): String {
        val member: Member = memberRepository.findByEmail(email)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.memberTaskException }
        return member.loginId
    }

    @Transactional
    fun setTemplatePassword(loginId: String, email: String): String {
        val member: Member = memberRepository.findByLoginIdAndEmail(loginId, email)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.memberTaskException }
        val templatePassword: String = PasswordUtil.generateTempPassword()
        member.pw = (passwordEncoder.encode(templatePassword))
        memberRepository.save(member)

        return templatePassword
    }

    @Transactional
    fun bulkInsertMembers(members: List<Member>, batchSize: Int = 1000) {
        members.forEachIndexed { index, member ->
            memberRepository.save(member)
            if (index % batchSize == 0 && index > 0) {
                memberRepository.flush()
                entityManager.clear()
            }
            memberRepository.flush()
            entityManager.clear()

        }

    }

}

