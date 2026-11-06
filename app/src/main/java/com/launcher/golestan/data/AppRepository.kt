package com.launcher.golestan.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.launcher.golestan.model.AppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

class AppRepository(
    private val context: Context,
    private val preferences: AppPreferences
) {
    private val packageManager: PackageManager = context.packageManager
    private val collator: Collator = Collator.getInstance(Locale.getDefault()).apply {
        strength = Collator.SECONDARY
    }

    suspend fun loadInstalledApps(): List<AppModel> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                launcherIntent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(launcherIntent, 0)
        }

        val myPackageName = context.packageName
        val favorites = preferences.getFavorites()

        val appList = mutableListOf<AppModel>()

        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName

            // Skip this launcher itself from the app drawer
            if (packageName == myPackageName) continue

            val activityName = resolveInfo.activityInfo.name
            val label = try {
                resolveInfo.loadLabel(packageManager).toString().trim().ifEmpty {
                    resolveInfo.activityInfo.name.substringAfterLast('.')
                }
            } catch (e: Exception) {
                packageName
            }

            val icon = try {
                resolveInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                null
            }

            val isSystem = try {
                (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            } catch (e: Exception) {
                false
            }

            val installTime = try {
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0)
                }
                packageInfo.firstInstallTime
            } catch (e: Exception) {
                0L
            }

            val isFav = favorites.contains(packageName)

            appList.add(
                AppModel(
                    label = label,
                    packageName = packageName,
                    activityName = activityName,
                    icon = icon,
                    isSystemApp = isSystem,
                    installTime = installTime,
                    isFavorite = isFav
                )
            )
        }

        // Sort alphabetically by label
        appList.sortWith { a, b -> collator.compare(a.label, b.label) }
        appList
    }
}
