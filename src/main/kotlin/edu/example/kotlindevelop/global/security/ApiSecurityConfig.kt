package edu.example.kotlindevelop.global.security


import jakarta.servlet.http.HttpServletRequest
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.slf4j.LoggerFactory

@Configuration
@EnableMethodSecurity
class ApiSecurityConfig {


    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/api/**")
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/adm/**").hasRole("ADMIN")
                    .requestMatchers(
                        "/api/v1/members/login",
                        "/api/v1/members/register",
                         "/api/**",
                        "/api/v1/members/refreshAccessToken",
                        "/api/v1/members/findId",
                        "/api/v1/members/findPW"
                    ).permitAll()
                    .anyRequest().authenticated()
            }




        return http.build()
    }
}
