package com.grace.app.ui

import com.grace.app.R
import com.grace.app.data.MealRepository
import com.grace.app.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun ensureTncState_setsDialogWhenNotAccepted() {
        val viewModel = MainViewModel(
            mealRepository = FakeMealRepository(),
            preferences = FakePreferences(false)
        )

        viewModel.ensureTncState()

        assertTrue(viewModel.uiState.value.showTncDialog)
    }

    @Test
    fun startProcessing_checkMeal_emitsPhotoResult() = runTest {
        val viewModel = MainViewModel(
            mealRepository = FakeMealRepository(isMealResult = true),
            preferences = FakePreferences(true)
        )

        viewModel.setPhotoUri("path")
        viewModel.setLoadingMode(LoadingMode.CHECK_MEAL)

        val eventDeferred = launch { viewModel.events.first() }
        viewModel.startProcessingIfNeeded()
        advanceUntilIdle()

        val mealPhoto = viewModel.uiState.value.mealPhoto
        assertEquals(R.string.his_grace_asks_text, mealPhoto?.title)
        assertTrue(eventDeferred.isCompleted)
    }

    @Test
    fun startProcessing_blessFailure_showsErrorAndNavigatesHome() = runTest {
        val viewModel = MainViewModel(
            mealRepository = FakeMealRepository(blessedUri = ""),
            preferences = FakePreferences(true)
        )

        viewModel.setPhotoUri("path")
        viewModel.setLoadingMode(LoadingMode.BLESS_PHOTO)

        val firstEvent = launch { viewModel.events.first() }
        viewModel.startProcessingIfNeeded()
        advanceUntilIdle()

        assertTrue(firstEvent.isCompleted)
    }
}

private class FakeMealRepository(
    private val isMealResult: Boolean = false,
    private val blessedUri: String = "blessed"
) : MealRepository {
    override suspend fun isMeal(photoUri: String): Boolean = isMealResult
    override suspend fun blessPhoto(photoUri: String): String = blessedUri
}

private class FakePreferences(initial: Boolean) : AppPreferences {
    override var isTncAccepted: Boolean = initial
}
