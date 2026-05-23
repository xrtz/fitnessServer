package com.example.myfitness.service

import com.example.myfitness.dto.UserRequest
import com.example.myfitness.dto.UserResponse
import com.example.myfitness.model.User
import com.example.myfitness.repository.UserRepository
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun registerOrGet(uid: String, request: UserRequest): UserResponse {
        return withRetry {
            val existing = userRepository.findByFirebaseUid(uid)
            val user = existing ?: userRepository.save(
                User(
                    firebaseUid = uid,
                    name        = request.name,
                    gender      = request.gender,
                    email       = request.email,
                    weight      = request.weight,
                    height      = request.height,
                    target      = request.target
                )
            )
            user.toResponse()
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
            user.name   = request.name
            user.gender = request.gender
            user.email  = request.email
            user.weight = request.weight
            user.height = request.height
            user.target = request.target
            userRepository.save(user).toResponse()
        }
    }

    private fun User.toResponse() = UserResponse(
        firebaseUid = firebaseUid,
        name        = name,
        gender      = gender,
        email       = email,
        weight      = weight,
        height      = height,
        target      = target
    )

    /**
     * Повторяет операцию при потере соединения с Neon.
     * Neon serverless иногда закрывает соединение между запросами.
     */
    private fun <T> withRetry(maxAttempts: Int = 3, block: () -> T): T {
        var lastException: Exception? = null
        repeat(maxAttempts) { attempt ->
            try {
                return block()
            } catch (e: DataAccessResourceFailureException) {
                lastException = e
                if (attempt < maxAttempts - 1) {
                    Thread.sleep(500L * (attempt + 1))
                }
            } catch (e: Exception) {
                if (e.message?.contains("Connection") == true ||
                    e.message?.contains("Соединение") == true) {
                    lastException = e
                    if (attempt < maxAttempts - 1) {
                        Thread.sleep(500L * (attempt + 1))
                    }
                } else {
                    throw e
                }
            }
        }
        throw lastException!!
    }
}