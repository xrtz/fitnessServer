package com.example.myfitness.repository

import com.example.myfitness.model.DayFood
import com.example.myfitness.model.FoodItem
import com.example.myfitness.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByFirebaseUid(uid: String): User?
}

@Repository
interface DayFoodRepository : JpaRepository<DayFood, Long> {
    fun findByFirebaseUidAndDate(uid: String, date: Int): DayFood?
}

@Repository
interface FoodItemRepository : JpaRepository<FoodItem, Long> {
    fun findAllByDayFoodId(dayFoodId: Long): List<FoodItem>
    fun deleteAllByDayFoodId(dayFoodId: Long)
}
