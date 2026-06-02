package com.example.myfitness.service

import com.example.myfitness.dto.AuthResponse
import com.example.myfitness.dto.LoginRequest
import com.example.myfitness.dto.RegisterRequest
import com.example.myfitness.dto.UserRequest
import com.example.myfitness.dto.UserResponse
import com.example.myfitness.model.User
import com.example.myfitness.repository.UserRepository
import com.example.myfitness.security.JwtService
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequest): AuthResponse {
        return withRetry {
            if (userRepository.findByEmail(request.email) != null)
                throw IllegalArgumentException("Email already registered")
            val uid = UUID.randomUUID().toString()
            val user = userRepository.save(
                User(
                    firebaseUid = uid,
                    name = request.name,
                    email = request.email,
                    passwordHash = passwordEncoder.encode(request.password),
                    gender = request.gender,
                    weight = request.weight,
                    height = request.height,
                    target = request.target
                )
            )
            AuthResponse(token = jwtService.generateToken(uid), user = user.toResponse())
        }
    }

    fun login(request: LoginRequest): AuthResponse {
        return withRetry {
            val user = userRepository.findByEmail(request.email)
                ?: throw NoSuchElementException("User not found")
            if (!passwordEncoder.matches(request.password, user.passwordHash))
                throw IllegalArgumentException("Invalid credentials")
            AuthResponse(token = jwtService.generateToken(user.firebaseUid), user = user.toResponse())
        }
    }

    fun getUser(uid: String): UserResponse {
        return withRetry {
            val user = userRepository.findByFirebaseUid(uid)
                ?: throw NoSuchElementException("User not found: $uid")
            user.toResponse()
        }
    }

    fun updateUser(uid: String, request: UserRequest): UserResponse {
        return withRetry {
            val user = userRepository.findByFirebaseUid(uid)
                ?: throw NoSuchElementException("User not found: $uid")
            user.name = request.name
            user.gender = request.gender
            user.email = request.email
            user.weight = request.weight
            user.height = request.height
            user.target = request.target
            userRepository.save(user).toResponse()
        }
    }

    private fun User.toResponse() = UserResponse(
        firebaseUid = firebaseUid,
        name = name,
        gender = gender,
        email = email,
        weight = weight,
        height = height,
        target = target
    )

    private fun <T> withRetry(maxAttempts: Int = 3, block: () -> T): T {
        var lastException: Exception? = null
        repeat(maxAttempts) { attempt ->
            try {
                return block()
            } catch (e: DataAccessResourceFailureException) {
                lastException = e
                if (attempt < maxAttempts - 1) Thread.sleep(500L * (attempt + 1))
            } catch (e: Exception) {
                if (e.message?.contains("Connection") == true ||
                    e.message?.contains("Соединение") == true
                ) {
                    lastException = e
                    if (attempt < maxAttempts - 1) Thread.sleep(500L * (attempt + 1))
                } else {
                    throw e
                }
            }
        }
        throw lastException!!
    }
}
