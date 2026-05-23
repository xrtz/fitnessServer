package com.example.myfitness.controller

import com.example.myfitness.dto.DayFoodRequest
import com.example.myfitness.dto.DayFoodResponse
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
    fun register(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val uid = currentUid()
        return try {
            val user = userService.registerOrGet(uid, request)
            ResponseEntity.ok(user)
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
            val user = userService.getUser(uid)
            ResponseEntity.ok(user)
        } catch (e: NoSuchElementException) {
            val auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().authentication
            val email = (auth.details as? Map<*, *>)?.get("email") as? String ?: ""

            val created = try {
                userService.registerOrGet(uid, UserRequest(
                    name   = email.substringBefore("@").ifBlank { "User" },
                    gender = 0,
                    email  = email,
                    weight = 0f,
                    height = 0f,
                    target = "maintain"
                ))
            } catch (ex: Exception) {
                return ResponseEntity.status(503).build()
            }
            ResponseEntity.ok(created)
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }

    @PutMapping("/me")
    fun updateMe(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        val uid = currentUid()
        return try {
            val user = userService.updateUser(uid, request)
            ResponseEntity.ok(user)
        } catch (e: NoSuchElementException) {
            val created = try {
                userService.registerOrGet(uid, request)
            } catch (ex: Exception) {
                return ResponseEntity.status(503).build()
            }
            ResponseEntity.ok(created)
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
            val day = dayFoodService.getDay(uid, epochDay)
            ResponseEntity.ok(day)
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }

    @PutMapping
    fun saveDay(@RequestBody request: DayFoodRequest): ResponseEntity<DayFoodResponse> {
        val uid = currentUid()
        return try {
            val day = dayFoodService.saveDay(uid, request)
            ResponseEntity.ok(day)
        } catch (e: Exception) {
            ResponseEntity.status(503).build()
        }
    }
}