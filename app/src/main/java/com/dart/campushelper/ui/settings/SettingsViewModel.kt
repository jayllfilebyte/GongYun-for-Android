package com.dart.campushelper.ui.settings

import android.appwidget.AppWidgetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dart.campushelper.BuildConfig
import com.dart.campushelper.CampusHelperApplication
import com.dart.campushelper.data.UserPreferenceRepository
import com.dart.campushelper.data.VALUES.DEFAULT_VALUE_ENABLE_SYSTEM_COLOR
import com.dart.campushelper.data.VALUES.DEFAULT_VALUE_IS_LOGIN
import com.dart.campushelper.data.VALUES.DEFAULT_VALUE_IS_PIN
import com.dart.campushelper.data.VALUES.DEFAULT_VALUE_SELECTED_DARK_MODE
import com.dart.campushelper.data.VALUES.DEFAULT_VALUE_USERNAME
import com.dart.campushelper.ui.MainActivity
import com.dart.campushelper.ui.pin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

data class SettingsUiState(
    val isLogin: Boolean = DEFAULT_VALUE_IS_LOGIN,
    val username: String = DEFAULT_VALUE_USERNAME,
    val enableSystemColor: Boolean = DEFAULT_VALUE_ENABLE_SYSTEM_COLOR,
    val selectedDarkMode: String = DEFAULT_VALUE_SELECTED_DARK_MODE,
    val isPin: Boolean = DEFAULT_VALUE_IS_PIN,
    val openLogoutConfirmDialog: Boolean = false,
    val openFeedbackUrlConfirmDialog: Boolean = false,
    val openSourceCodeUrlConfirmDialog: Boolean = false,
    val appVersion: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(
        SettingsUiState(
            appVersion = BuildConfig.VERSION_NAME
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val usernameStateFlow = userPreferenceRepository.observeUsername().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        runBlocking {
            userPreferenceRepository.observeUsername().first()
        }
    )

    private val enableSystemColorStateFlow =
        userPreferenceRepository.observeEnableSystemColor().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            runBlocking {
                userPreferenceRepository.observeEnableSystemColor().first()
            }
        )

    private val selectedDarkModeStateFlow =
        userPreferenceRepository.observeSelectedDarkMode().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            runBlocking {
                userPreferenceRepository.observeSelectedDarkMode().first()
            }
        )

    private val isPinStateFlow = userPreferenceRepository.observeIsPin().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        runBlocking {
            userPreferenceRepository.observeIsPin().first()
        }
    )

    private val isLoginStateFlow: StateFlow<Boolean> = userPreferenceRepository.observeIsLogin().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = runBlocking {
            userPreferenceRepository.observeIsLogin().first()
        }
    )

    init {
        viewModelScope.launch {
            usernameStateFlow.collect { value ->
                _uiState.update {
                    it.copy(username = value)
                }
            }
        }
        viewModelScope.launch {
            isLoginStateFlow.collect { value ->
                _uiState.update {
                    it.copy(isLogin = value)
                }
            }
        }
        viewModelScope.launch {
            enableSystemColorStateFlow.collect { value ->
                _uiState.update {
                    it.copy(enableSystemColor = value ?: false)
                }
            }
        }
        viewModelScope.launch {
            selectedDarkModeStateFlow.collect { value ->
                _uiState.update {
                    it.copy(selectedDarkMode = value)
                }
            }
        }
        viewModelScope.launch {
            isPinStateFlow.collect { value ->
                _uiState.update {
                    it.copy(isPin = value ?: false)
                }
            }
        }
    }

    fun clearCookies() {
        viewModelScope.launch {
            userPreferenceRepository.changeCookies(emptyList())
            MainActivity.snackBarHostState.showSnackbar("Cookies 已清除，请开始调试")
        }
    }

    fun changeEnableSystemColor(enable: Boolean) {
        viewModelScope.launch {
            userPreferenceRepository.changeEnableSystemColor(enable)
        }
    }

    fun changeSelectedDarkMode(darkMode: String) {
        viewModelScope.launch {
            userPreferenceRepository.changeSelectedDarkMode(darkMode)
        }
    }

    fun changeIsPin(isPin: Boolean) {
        _uiState.update {
            it.copy(isPin = isPin)
        }
        viewModelScope.launch {
            userPreferenceRepository.changeIsPin(isPin)
        }
        if (isPin) {
            val widgetManager = AppWidgetManager.getInstance(CampusHelperApplication.context)
            // Get a list of our app widget providers to retrieve their info
            val widgetProviders =
                widgetManager.getInstalledProvidersForPackage(
                    CampusHelperApplication.context.packageName,
                    null
                )
            widgetProviders[0].pin(CampusHelperApplication.context)
        }
    }

    fun onShowLogoutConfirmDialogRequest() {
        _uiState.update {
            it.copy(openLogoutConfirmDialog = true)
        }
    }

    fun onHideLogoutConfirmDialogRequest() {
        _uiState.update {
            it.copy(openLogoutConfirmDialog = false)
        }
    }

    fun onShowFeedbackUrlConfirmDialogRequest() {
        _uiState.update {
            it.copy(openFeedbackUrlConfirmDialog = true)
        }
    }

    fun onHideFeedbackUrlConfirmDialogRequest() {
        _uiState.update {
            it.copy(openFeedbackUrlConfirmDialog = false)
        }
    }

    fun onShowSourceCodeUrlConfirmDialogRequest() {
        _uiState.update {
            it.copy(openSourceCodeUrlConfirmDialog = true)
        }
    }

    fun onHideSourceCodeUrlConfirmDialogRequest() {
        _uiState.update {
            it.copy(openSourceCodeUrlConfirmDialog = false)
        }
    }
}
