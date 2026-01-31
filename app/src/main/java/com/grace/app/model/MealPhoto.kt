package com.grace.app.model

import androidx.annotation.StringRes

data class MealPhoto(
    val photoUri: String?,
    @StringRes val title: Int,
    @StringRes val subTitle: Int,
    @StringRes val leftButtonText: Int,
    @StringRes val rightButtonText: Int
)
