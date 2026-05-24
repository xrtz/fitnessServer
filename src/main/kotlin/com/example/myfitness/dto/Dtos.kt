package com.example.myfitness.dto

data class RegisterRequest(
    val name     : String,
    val email    : String,
    val password : String,
    val gender   : Int    = 0,
    val weight   : Float  = 0f,
    val height   : Float  = 0f,
    val target   : String = ""
)

data class LoginRequest(
    val email    : String,
    val password : String
)

data class AuthResponse(
    val token : String,
    val user  : UserResponse
)

data class UserRequest(
    val name   : String,
    val gender : Int,
    val email  : String,
    val weight : Float,
    val height : Float,
    val target : String
)

data class UserResponse(
    val firebaseUid : String,
    val name        : String,
    val gender      : Int,
    val email       : String,
    val weight      : Float,
    val height      : Float,
    val target      : String
)

data class FoodItemDto(
    val id            : Long   = 0,
    val name          : String,
    val weight        : Float,
    val calories      : Int,
    val typeOfMeal    : String,
    val protein       : Float,
    val fats          : Float,
    val carbohydrates : Float
)

data class DayFoodRequest(
    val date          : Int,
    val calories      : Float,
    val protein       : Float,
    val fats          : Float,
    val carbohydrates : Float,
    val foodItems     : List<FoodItemDto>
)

data class DayFoodResponse(
    val id            : Long,
    val date          : Int,
    val calories      : Float,
    val protein       : Float,
    val fats          : Float,
    val carbohydrates : Float,
    val breakfast     : List<FoodItemDto>,
    val lunch         : List<FoodItemDto>,
    val dinner        : List<FoodItemDto>,
    val snacks        : List<FoodItemDto>
)
