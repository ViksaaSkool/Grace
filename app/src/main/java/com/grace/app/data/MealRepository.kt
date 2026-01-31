package com.grace.app.data

interface MealRepository {
    suspend fun isMeal(photoUri: String): Boolean
    suspend fun blessPhoto(photoUri: String): String
}
