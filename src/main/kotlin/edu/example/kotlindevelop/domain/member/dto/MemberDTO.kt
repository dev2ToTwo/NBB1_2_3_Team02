package edu.example.kotlindevelop.domain.member.dto

import com.fasterxml.jackson.annotation.JsonProperty
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.pagination.Cursor
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

class MemberDTO {
    class NaverResponse(private val attribute: Map<String, Any>) {
        val provider: String = "naver"
        val providerId: String = attribute["id"] as String
        val email: String = attribute["email"] as String
        val name: String = attribute["name"] as String
        val nickname: String = attribute["nickname"] as String
        var profileImage: String = attribute["profile_image"] as String
    }

    class CustomOAuth2User(
        private var oAuth2Response: NaverResponse,
        private var role: String
    ): OAuth2User {
        override fun getName(): String {
            return oAuth2Response.name
        }
        override fun getAttributes(): Map<String, Any> {
            return mapOf(
                "provider" to oAuth2Response.provider,
                "providerId" to oAuth2Response.providerId,
                "email" to oAuth2Response.email,
                "name" to oAuth2Response.name,
                "nickname" to oAuth2Response.nickname,
                "profileImage" to oAuth2Response.profileImage,
            )
        }
        override fun getAuthorities(): Collection<GrantedAuthority> {
            return listOf(GrantedAuthority { role })
        }
        fun getUsername(): String {
            return "${oAuth2Response.provider} ${oAuth2Response.providerId}"
        }
    }


    data class CreateRequestDto(
        @field:NotBlank(message = "로그인 ID는 필수 입력 값 입니다")
        var loginId: String,
        @field:NotBlank(message = "이메일은 필수 입력 값 입니다")
        var email: String,
        @field:NotBlank(message = "비밀번호는 필수 입력 값 입니다")
        var pw: String,
        @field:NotBlank(message = "닉네임은 필수 입력 값 입니다")
        var name: String,
        @JsonProperty("mImage")
        var mImage: String? = null
    ) {
        fun toEntity(): Member {
            // Member 객체를 초기화
            return Member(
                loginId = loginId,
                email = email,
                pw = pw,
                name = name,
                mImage = mImage
            )
        }
    }


    data class StringResponseDto( var message: String)

    data class Update (
        var id: Long,
        var loginId: String? = null,
        var pw: String? = null,
        var name: String? = null,
        var mImage: String? = null,
        var email: String? = null
    )


    data class LoginRequestDto (
        @field:NotBlank(message = "로그인 ID를 입력해주세요")
        var loginId: String,
        @NotBlank(message = "비밀번호를 입력해주세요")
        var pw: String
    )


    data class LoginResponseDto(
        var id: Long,
        var loginId: String,
        var name: String,
        var mImage: String,
        var accessToken: String? = null,
        var refreshToken: String? = null,
        var userName: String? = null
    ) {
        constructor(member: Member) : this(
            // id는 AutoIncrement 로 생성하기 때문에 Null허용 객체 이지만, Response 과정에서는 null일 가능성이 없기 때문에 대입 값으로 0 설정
            id = member.id?:0,
            loginId = member.loginId,
            name = member.name,
            mImage = member.mImage ?: "default_image.png",
            userName = member.userName ?: ""
        )

    }


    data class RefreshAccessTokenRequestDto ( var refreshToken: String )
    data class RefreshAccessTokenResponseDto( var accessToken: String, var message: String)

    data class Delete (
        var loginId: String,
        var pw: String
    )

    data class Response(
        var id: Long,
        var loginId: String,
        var email: String,
        var pw: String,
        var name: String,
        var mImage: String,
        var createdAt: LocalDateTime,
        var modifiedAt: LocalDateTime
    ) {
        constructor(member: Member) : this(
            id = member.id ?: throw IllegalArgumentException("Member ID cannot be null"),
            loginId = member.loginId,
            pw = member.pw,
            name = member.name,
            mImage = member.mImage ?: "api/v1/member/upload/defaultImageUrl.jpg",
            createdAt = member.createdAt ?: LocalDateTime.now(),
            modifiedAt = member.modifiedAt ?: LocalDateTime.now(),
            email = member.email
        )
    }

    data class ChangeImage (
        var id: Long,
        var mImage: MultipartFile
    )
    data class ChangeImageResponse (
        var id: Long,
        var mImage: String
    )

    data class LogoutResponseDto ( var message: String)


    data class FindPWRequestDto (
         var loginId: String,
         var email: String
    )

    data class MemberCursorRequest(
        val lastCreatedAt: LocalDateTime?,
        val lastId : Long?,
        val limit: Int?

    )

    data class MemberCursorResponse(
        val members : List<Response>,
        val nextCursor : Cursor?

    )



}
//    @Data
        //    @AllArgsConstructor
        //    public static class ProductGet {
        //         Long id;
        //         List<Product> productList = new ArrayList<>();
        //    }
        //
        //    @Data
        //    @AllArgsConstructor
        //    public static class OrdersGet {
        //         List<Orders> ordersList = new ArrayList<>();
        //    }
