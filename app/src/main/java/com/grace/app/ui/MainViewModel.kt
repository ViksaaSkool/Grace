package com.grace.app.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.app.R
import com.grace.app.data.MealRepository
import com.grace.app.model.MealPhoto
import com.grace.app.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val preferences: AppPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events = _events.asSharedFlow()

    fun ensureTncState() {
        if (!preferences.isTncAccepted) {
            _uiState.update { it.copy(showTncDialog = true) }
        }
    }

    fun acceptTnc() {
        preferences.isTncAccepted = true
        _uiState.update { it.copy(showTncDialog = false) }
    }

    fun dismissTnc() {
        _uiState.update { it.copy(showTncDialog = false) }
    }

    fun setPhotoUri(uri: String) {
        _uiState.update { it.copy(currentPhotoUri = uri, mealPhoto = null) }
    }

    fun setLoadingMode(mode: LoadingMode?) {
        _uiState.update { it.copy(loadingMode = mode) }
    }

    fun startProcessingIfNeeded() {
        val state = _uiState.value
        if (state.processing || state.loadingMode == null || state.currentPhotoUri.isNullOrBlank()) {
            return
        }
        _uiState.update { it.copy(processing = true) }
        viewModelScope.launch {
            try {
                when (state.loadingMode) {
                    LoadingMode.CHECK_MEAL -> {
                        val isMeal = mealRepository.isMeal(state.currentPhotoUri)
                        val mealPhoto = if (isMeal) {
                            MealPhoto(
                                photoUri = state.currentPhotoUri,
                                title = R.string.his_grace_asks_text,
                                subTitle = R.string.do_you_want_to_text,
                                leftButtonText = R.string.no_text,
                                rightButtonText = R.string.yes_text
                            )
                        } else {
                            MealPhoto(
                                photoUri = null,
                                title = R.string.his_grace_angered_text,
                                subTitle = R.string.not_a_meal_text,
                                leftButtonText = R.string.feel_wraith_text,
                                rightButtonText = R.string.another_try_text
                            )
                        }
                        _uiState.update {
                            it.copy(mealPhoto = mealPhoto, loadingMode = null, processing = false)
                        }
                        _events.emit(MainEvent.NavigateToPhoto)
                    }

                    LoadingMode.BLESS_PHOTO -> {
                        val blessedUri = mealRepository.blessPhoto(state.currentPhotoUri)
                        if (blessedUri.isNotBlank()) {
                            val mealPhoto = MealPhoto(
                                photoUri = blessedUri,
                                title = R.string.his_grace_approves_text,
                                subTitle = R.string.your_meal_is_blessed_text,
                                leftButtonText = R.string.done_text,
                                rightButtonText = R.string.share_text
                            )
                            _uiState.update {
                                it.copy(
                                    mealPhoto = mealPhoto,
                                    loadingMode = null,
                                    processing = false
                                )
                            }
                            _events.emit(MainEvent.NavigateToPhoto)
                        } else {
                            _uiState.update { it.copy(loadingMode = null, processing = false) }
                            _events.emit(MainEvent.ShowSnackbar(R.string.something_went_wrong_text))
                            _events.emit(MainEvent.NavigateToHome)
                        }
                    }

                    null -> Unit
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(loadingMode = null, processing = false) }
                _events.emit(MainEvent.ShowSnackbar(R.string.something_went_wrong_text))
                _events.emit(MainEvent.NavigateToHome)
            }
        }
    }

    fun requestBlessing() {
        _uiState.update { it.copy(loadingMode = LoadingMode.BLESS_PHOTO) }
    }
}

data class MainUiState(
    val currentPhotoUri: String? = null,
    val loadingMode: LoadingMode? = null,
    val processing: Boolean = false,
    val mealPhoto: MealPhoto? = null,
    val showTncDialog: Boolean = false
)

enum class LoadingMode(@StringRes val messageRes: Int) {
    CHECK_MEAL(R.string.let_me_see_text),
    BLESS_PHOTO(R.string.blessing_photo_text)
}

sealed interface MainEvent {
    data object NavigateToPhoto : MainEvent
    data object NavigateToHome : MainEvent
    data class ShowSnackbar(@StringRes val messageRes: Int) : MainEvent
}
