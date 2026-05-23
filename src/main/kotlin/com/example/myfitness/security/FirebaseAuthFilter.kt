package com.example.myfitness.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
@Component
class FirebaseAuthFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader("Authorization")

        try {

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                val token = authHeader
                    .removePrefix("Bearer ")
                    .trim()

                val decodedToken = FirebaseAuth
                    .getInstance()
                    .verifyIdToken(token)

                val authentication =
                    UsernamePasswordAuthenticationToken(
                        decodedToken.uid,
                        null,
                        emptyList()
                    )

                SecurityContextHolder
                    .getContext()
                    .authentication = authentication
            }

        } catch (e: Exception) {

            println("Firebase auth error: ${e.message}")
        }

        filterChain.doFilter(request, response)
    }
}