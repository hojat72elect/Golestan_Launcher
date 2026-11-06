package com.launcher.golestan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.launcher.golestan.data.AppPreferences
import com.launcher.golestan.data.AppRepository
import com.launcher.golestan.model.AppModel
import com.launcher.golestan.model.LauncherTab
import com.launcher.golestan.model.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = AppPreferences(application)
    private val repository = AppRepository(application, preferences)

    private val _allApps = MutableStateFlow<List<AppModel>>(emptyList())
    val allApps: StateFlow<List<AppModel>> = _allApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentTab = MutableStateFlow(LauncherTab.ALL)
    val currentTab: StateFlow<LauncherTab> = _currentTab.asStateFlow()

    private val _selectedAppForMenu = MutableStateFlow<AppModel?>(null)
    val selectedAppForMenu: StateFlow<AppModel?> = _selectedAppForMenu.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val columnsCount: StateFlow<Int> = preferences.columnsFlow
    val viewMode: StateFlow<ViewMode> = preferences.viewModeFlow
    val showLabels: StateFlow<Boolean> = preferences.showLabelsFlow
    val showSystemApps: StateFlow<Boolean> = preferences.showSystemAppsFlow

    // Filtered apps combining search, system app preference, and active tab
    val filteredApps: StateFlow<List<AppModel>> = combine(
        _allApps,
        _searchQuery,
        _currentTab,
        showSystemApps
    ) { apps, query, tab, includeSystem ->
        var list = apps

        if (!includeSystem) {
            list = list.filter { !it.isSystemApp }
        }

        if (tab == LauncherTab.FAVORITES) {
            list = list.filter { it.isFavorite }
        }

        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            val lowercaseQuery = trimmed.lowercase()
            list = list.filter {
                it.label.lowercase().contains(lowercaseQuery) ||
                        it.packageName.lowercase().contains(lowercaseQuery)
            }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesCount: StateFlow<Int> = _allApps.combine(preferences.favoritesFlow) { apps, favs ->
        apps.count { favs.contains(it.packageName) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loaded = repository.loadInstalledApps()
                _allApps.value = loaded
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTabChange(tab: LauncherTab) {
        _currentTab.value = tab
    }

    fun toggleFavorite(app: AppModel) {
        preferences.toggleFavorite(app.packageName)
        val updatedFavs = preferences.getFavorites()
        _allApps.value = _allApps.value.map {
            if (it.packageName == app.packageName) {
                it.copy(isFavorite = updatedFavs.contains(it.packageName))
            } else {
                it
            }
        }
        // Update selected app if menu is open
        _selectedAppForMenu.value?.let { selected ->
            if (selected.packageName == app.packageName) {
                _selectedAppForMenu.value = selected.copy(isFavorite = updatedFavs.contains(app.packageName))
            }
        }
    }

    fun openAppMenu(app: AppModel) {
        _selectedAppForMenu.value = app
    }

    fun closeAppMenu() {
        _selectedAppForMenu.value = null
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun setColumnsCount(columns: Int) {
        preferences.setColumns(columns)
    }

    fun setViewMode(mode: ViewMode) {
        preferences.setViewMode(mode)
    }

    fun setShowLabels(show: Boolean) {
        preferences.setShowLabels(show)
    }

    fun setShowSystemApps(show: Boolean) {
        preferences.setShowSystemApps(show)
    }
}
