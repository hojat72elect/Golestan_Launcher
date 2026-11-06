package com.launcher.golestan

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.launcher.golestan.model.AppModel
import com.launcher.golestan.model.LauncherTab
import com.launcher.golestan.receiver.PackageChangeReceiver
import com.launcher.golestan.ui.screens.LauncherScreen
import com.launcher.golestan.ui.theme.GolestanLauncherTheme
import com.launcher.golestan.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()
    private lateinit var packageReceiver: PackageChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup live package change monitoring
        packageReceiver = PackageChangeReceiver {
            viewModel.loadApps()
        }
        PackageChangeReceiver.register(this, packageReceiver)

        setContent {
            GolestanLauncherTheme {
                val searchQuery by viewModel.searchQuery.collectAsState()
                val selectedApp by viewModel.selectedAppForMenu.collectAsState()
                val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
                val currentTab by viewModel.currentTab.collectAsState()

                // Custom Back Handling for Launcher
                BackHandler(
                    enabled = isSettingsOpen || selectedApp != null || searchQuery.isNotEmpty() || currentTab != LauncherTab.ALL
                ) {
                    when {
                        isSettingsOpen -> viewModel.closeSettings()
                        selectedApp != null -> viewModel.closeAppMenu()
                        searchQuery.isNotEmpty() -> viewModel.onSearchQueryChange("")
                        currentTab != LauncherTab.ALL -> viewModel.onTabChange(LauncherTab.ALL)
                    }
                }

                LauncherScreen(
                    viewModel = viewModel,
                    onLaunchApp = ::launchApp,
                    onAppInfo = ::openAppInfo,
                    onUninstallApp = ::uninstallApp,
                    onOpenPlayStore = ::openInPlayStore,
                    onShareApp = ::shareApp
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PackageChangeReceiver.unregister(this, packageReceiver)
    }

    private fun launchApp(app: AppModel) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(app.packageName, app.activityName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to getLaunchIntentForPackage
            try {
                val fallbackIntent = packageManager.getLaunchIntentForPackage(app.packageName)
                if (fallbackIntent != null) {
                    fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(fallbackIntent)
                } else {
                    Toast.makeText(this, "Unable to launch ${app.label}", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(this, "Failed to launch app: ${ex.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openAppInfo(app: AppModel) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", app.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open app info", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uninstallApp(app: AppModel) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", app.packageName, null)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not initiate uninstall", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openInPlayStore(app: AppModel) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${app.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${app.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(webIntent)
        }
    }

    private fun shareApp(app: AppModel) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out \"${app.label}\": https://play.google.com/store/apps/details?id=${app.packageName}"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share ${app.label}")
            startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to share app", Toast.LENGTH_SHORT).show()
        }
    }
}
