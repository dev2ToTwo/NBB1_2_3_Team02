package edu.example.kotlindevelop.global.jwt


import edu.example.kotlindevelop.global.jwt.JwtUtil
import edu.example.kotlindevelop.global.security.SecurityUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter (
    private val jwtUtil: JwtUtil
): OncePerRequestFilter() {

    companion object {
        private val logger = LoggerFactory.getLogger(SecurityUser::class.java)  // 로거 생성
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val bearerToken = request.getHeader("Authorization")


        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            try {

                val token = bearerToken.substring("Bearer ".length)


                val claims: Claims = jwtUtil.decode(token)


                val data = claims["data"] as Map<String, Any>
                val id = (data["id"] as String).toLong()
                val loginId = data["loginId"] as String


                val authorities: List<GrantedAuthority> = (data["authorities"] as List<String>)
                    .map { SimpleGrantedAuthority(it) }


                val user = SecurityUser(id, loginId, "", authorities)


                val auth: Authentication = UsernamePasswordAuthenticationToken(user, user.password, user.authorities)


                SecurityContextHolder.getContext().authentication = auth
            } catch (e: SignatureException) {
                // 서명 오류가 발생한 경우 로그를 남기고 400 오류를 응답합니다.
                logger.debug("유효하지 않는 JWT 토큰", e)
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 토큰입니다.")
                return
            } catch (e: MalformedJwtException) {
                // 잘못된 형식의 JWT 토큰인 경우 로그를 남기고 400 오류를 응답합니다.
                logger.debug("유효하지 않는 JWT 토큰", e)
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 토큰입니다.")
                return
            } catch (e: UnsupportedJwtException) {
                // 지원되지 않는 JWT 토큰인 경우 로그를 남기고 400 오류를 응답합니다.
                logger.debug("유효하지 않는 JWT 토큰", e)
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 토큰입니다.")
                return
            } catch (e: IllegalArgumentException) {
                // 잘못된 JWT 토큰이 전달된 경우 로그를 남기고 400 오류를 응답합니다.
                logger.debug("유효하지 않는 JWT 토큰", e)
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 토큰입니다.")
                return
            } catch (e: ExpiredJwtException) {
                // JWT 토큰이 만료된 경우 로그를 남기고 401 오류를 응답합니다.
                logger.debug("만료된 JWT 토큰", e)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access 토큰이 만료되었습니다")
                return
            }


        }


        filterChain.doFilter(request, response)
    }
}

