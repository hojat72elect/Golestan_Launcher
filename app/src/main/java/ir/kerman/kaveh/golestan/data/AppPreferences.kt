package ir.kerman.kaveh.golestan.data

import android.content.Context
import android.content.SharedPreferences
import ir.kerman.kaveh.golestan.model.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("golestan_launcher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorite_packages"
        private const val KEY_COLUMNS = "grid_columns"
        private const val KEY_VIEW_MODE = "view_mode"
        private const val KEY_SHOW_LABELS = "show_labels"
        private const val KEY_SHOW_SYSTEM_APPS = "show_system_apps"
        private const val DEFAULT_COLUMNS = 4
    }

    private val _favoritesFlow = MutableStateFlow(getFavorites())
    val favoritesFlow: StateFlow<Set<String>> = _favoritesFlow.asStateFlow()

    private val _columnsFlow = MutableStateFlow(getColumns())
    val columnsFlow: StateFlow<Int> = _columnsFlow.asStateFlow()

    private val _viewModeFlow = MutableStateFlow(getViewMode())
    val viewModeFlow: StateFlow<ViewMode> = _viewModeFlow.asStateFlow()

    private val _showLabelsFlow = MutableStateFlow(getShowLabels())
    val showLabelsFlow: StateFlow<Boolean> = _showLabelsFlow.asStateFlow()

    private val _showSystemAppsFlow = MutableStateFlow(getShowSystemApps())
    val showSystemAppsFlow: StateFlow<Boolean> = _showSystemAppsFlow.asStateFlow()

    fun getFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun toggleFavorite(packageName: String) {
        val current = getFavorites().toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        _favoritesFlow.value = current
    }

    fun isFavorite(packageName: String): Boolean {
        return getFavorites().contains(packageName)
    }

    fun getColumns(): Int {
        return prefs.getInt(KEY_COLUMNS, DEFAULT_COLUMNS)
    }

    fun setColumns(columns: Int) {
        val clamped = columns.coerceIn(3, 6)
        prefs.edit().putInt(KEY_COLUMNS, clamped).apply()
        _columnsFlow.value = clamped
    }

    fun getViewMode(): ViewMode {
        val name = prefs.getString(KEY_VIEW_MODE, ViewMode.GRID.name) ?: ViewMode.GRID.name
        return try {
            ViewMode.valueOf(name)
        } catch (e: Exception) {
            ViewMode.GRID
        }
    }

    fun setViewMode(mode: ViewMode) {
        prefs.edit().putString(KEY_VIEW_MODE, mode.name).apply()
        _viewModeFlow.value = mode
    }

    fun getShowLabels(): Boolean {
        return prefs.getBoolean(KEY_SHOW_LABELS, true)
    }

    fun setShowLabels(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_LABELS, show).apply()
        _showLabelsFlow.value = show
    }

    fun getShowSystemApps(): Boolean {
        return prefs.getBoolean(KEY_SHOW_SYSTEM_APPS, true)
    }

    fun setShowSystemApps(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_SYSTEM_APPS, show).apply()
        _showSystemAppsFlow.value = show
    }
}
