package ir.kerman.kaveh.golestan.model

import android.graphics.drawable.Drawable

/**
 * Represents an installed application on the device.
 */
data class AppModel(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable?,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val isFavorite: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppModel) return false
        return packageName == other.packageName && activityName == other.activityName
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + activityName.hashCode()
        return result
    }
}

enum class ViewMode {
    GRID,
    LIST
}

enum class LauncherTab {
    ALL,
    FAVORITES
}
