package com.example.myfitness.service

import com.example.myfitness.dto.DayFoodRequest
import com.example.myfitness.dto.DayFoodResponse
import com.example.myfitness.dto.FoodItemDto
import com.example.myfitness.model.DayFood
import com.example.myfitness.model.FoodItem
import com.example.myfitness.repository.DayFoodRepository
import com.example.myfitness.repository.FoodItemRepository
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DayFoodService(
    private val dayFoodRepository: DayFoodRepository,
    private val foodItemRepository: FoodItemRepository
) {

    fun getDay(uid: String, date: Int): DayFoodResponse {
        return withRetry {
            val day = dayFoodRepository.findByFirebaseUidAndDate(uid, date)
                ?: return@withRetry emptyDayResponse(date)
            day.toResponse()
        }
    }

    @Transactional
    fun saveDay(uid: String, request: DayFoodRequest): DayFoodResponse {
        return withRetry {
            val day = dayFoodRepository.findByFirebaseUidAndDate(uid, request.date)
                ?: DayFood(firebaseUid = uid, date = request.date)

            day.calories = request.calories
            day.protein = request.protein
            day.fats = request.fats
            day.carbohydrates = request.carbohydrates

            val savedDay = dayFoodRepository.save(day)

            foodItemRepository.deleteAllByDayFoodId(savedDay.id)

            val newItems = request.foodItems.map { dto ->
                FoodItem(
                    dayFood = savedDay,
                    name = dto.name,
                    weight = dto.weight,
                    calories = dto.calories,
                    typeOfMeal = dto.typeOfMeal,
                    protein = dto.protein,
                    fats = dto.fats,
                    carbohydrates = dto.carbohydrates
                )
            }
            foodItemRepository.saveAll(newItems)

            dayFoodRepository.findByFirebaseUidAndDate(uid, request.date)!!.toResponse()
        }
    }

    private fun DayFood.toResponse(): DayFoodResponse {
        val items = foodItemRepository.findAllByDayFoodId(id)
        return DayFoodResponse(
            id = id,
            date = date,
            calories = calories,
            protein = protein,
            fats = fats,
            carbohydrates = carbohydrates,
            breakfast = items.filter { it.typeOfMeal == "breakfast" }.map { it.toDto() },
            lunch = items.filter { it.typeOfMeal == "lunch" }.map { it.toDto() },
            dinner = items.filter { it.typeOfMeal == "dinner" }.map { it.toDto() },
            snacks = items.filter { it.typeOfMeal == "snacks" }.map { it.toDto() }
        )
    }

    private fun FoodItem.toDto() = FoodItemDto(
        id = id,
        name = name,
        weight = weight,
        calories = calories,
        typeOfMeal = typeOfMeal,
        protein = protein,
        fats = fats,
        carbohydrates = carbohydrates
    )

    private fun emptyDayResponse(date: Int) = DayFoodResponse(
        id = 0, date = date,
        calories = 0f, protein = 0f, fats = 0f, carbohydrates = 0f,
        breakfast = emptyList(), lunch = emptyList(),
        dinner = emptyList(), snacks = emptyList()
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
                } else throw e
            }
        }
        throw lastException!!
    }
}