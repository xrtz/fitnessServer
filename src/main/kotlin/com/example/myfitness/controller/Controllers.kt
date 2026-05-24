package com.example.myfitness.controller

import com.example.myfitness.dto.AuthResponse
import com.example.myfitness.dto.DayFoodRequest
import com.example.myfitness.dto.DayFoodResponse
import com.example.myfitness.dto.LoginRequest
import com.example.myfitness.dto.RegisterRequest
import com.example.myfitness.dto.UserRequest
import com.example.myfitness.dto.UserResponse
import com.example.myfitness.service.DayFoodService
import com.example.myfitness.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

private fun currentUid(): String =
    SecurityContextHolder.getContext().authentication.name


@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return try {
            ResponseEntity.ok(userService.register(request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(409).build()
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            ResponseEntity.ok(userService.login(request))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).build()
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }
}


@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    fun getMe(): ResponseEntity<UserResponse> {
        val uid = currentUid()
        return try {
            ResponseEntity.ok(userService.getUser(uid))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).build()
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }

    @PutMapping("/me")
    fun updateMe(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val uid = currentUid()
        return try {
            ResponseEntity.ok(userService.updateUser(uid, request))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).build()
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }
}


@RestController
@RequestMapping("/api/days")
class DayFoodController(private val dayFoodService: DayFoodService) {

    @GetMapping("/{epochDay}")
    fun getDay(@PathVariable epochDay: Int): ResponseEntity<DayFoodResponse> {
        val uid = currentUid()
        return try {
            ResponseEntity.ok(dayFoodService.getDay(uid, epochDay))
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }

    @PutMapping
    fun saveDay(@RequestBody request: DayFoodRequest): ResponseEntity<DayFoodResponse> {
        val uid = currentUid()
        return try {
            ResponseEntity.ok(dayFoodService.saveDay(uid, request))
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }
}
