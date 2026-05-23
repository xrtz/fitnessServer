package com.example.myfitness.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "firebase_uid", nullable = false, unique = true)
    val firebaseUid: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Column
    var gender: Int = 0,

    @Column
    var email: String = "",

    @Column
    var weight: Float = 0f,

    @Column
    var height: Float = 0f,

    @Column
    var target: String = ""
)



@Entity
@Table(
    name = "day_food",
    uniqueConstraints = [UniqueConstraint(columnNames = ["firebase_uid", "date"])]
)
data class DayFood(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "firebase_uid", nullable = false)
    val firebaseUid: String = "",

    @Column(nullable = false)
    val date: Int = 0,

    @Column var calories: Float = 0f,
    @Column var protein: Float = 0f,
    @Column var fats: Float = 0f,
    @Column var carbohydrates: Float = 0f,

    @OneToMany(mappedBy = "dayFood", cascade = [CascadeType.ALL], orphanRemoval = true)
    var foodItems: MutableList<FoodItem> = mutableListOf()
)



@Entity
@Table(name = "food_items")
data class FoodItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_food_id", nullable = false)
    var dayFood: DayFood? = null,

    @Column(nullable = false) var name: String = "",
    @Column var weight: Float = 0f,
    @Column var calories: Int = 0,
    @Column(name = "type_of_meal") var typeOfMeal: String = "",
    @Column var protein: Float = 0f,
    @Column var fats: Float = 0f,
    @Column var carbohydrates: Float = 0f
)