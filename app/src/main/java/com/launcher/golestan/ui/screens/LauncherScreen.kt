package com.launcher.golestan.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.launcher.golestan.R
import com.launcher.golestan.model.AppModel
import com.launcher.golestan.model.LauncherTab
import com.launcher.golestan.model.ViewMode
import com.launcher.golestan.ui.components.AppContextMenu
import com.launcher.golestan.ui.components.AppGridItem
import com.launcher.golestan.ui.components.AppListItem
import com.launcher.golestan.ui.components.HomeClockWidget
import com.launcher.golestan.ui.components.SearchBarComponent
import com.launcher.golestan.ui.components.SettingsBottomSheet
import com.launcher.golestan.viewmodel.LauncherViewModel

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    onLaunchApp: (AppModel) -> Unit,
    onAppInfo: (AppModel) -> Unit,
    onUninstallApp: (AppModel) -> Unit,
    onOpenPlayStore: (AppModel) -> Unit,
    onShareApp: (AppModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val favoritesCount by viewModel.favoritesCount.collectAsState()
    val columnsCount by viewModel.columnsCount.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val showSystemApps by viewModel.showSystemApps.collectAsState()
    val selectedAppForMenu by viewModel.selectedAppForMenu.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Clock & Greeting Widget (visible when not actively searching)
            AnimatedVisibility(
                visible = searchQuery.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                HomeClockWidget()
            }

            // Nova-Style Floating Search Bar
            SearchBarComponent(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onOpenSettings = viewModel::openSettings
            )

            // Category Tabs (All Apps vs Favorites)
            TabRowSection(
                currentTab = currentTab,
                allAppsCount = allApps.size,
                favoritesCount = favoritesCount,
                onTabSelect = viewModel::onTabChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Apps Display Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    filteredApps.isEmpty() -> {
                        EmptyAppsState(
                            query = searchQuery,
                            onClearQuery = { viewModel.onSearchQueryChange("") }
                        )
                    }

                    viewMode == ViewMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columnsCount),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(
                                items = filteredApps,
                                key = { "${it.packageName}/${it.activityName}" }
                            ) { app ->
                                AppGridItem(
                                    app = app,
                                    showLabel = showLabels,
                                    onClick = { onLaunchApp(app) },
                                    onLongClick = { viewModel.openAppMenu(app) }
                                )
                            }
                        }
                    }

                    viewMode == ViewMode.LIST -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(
                                items = filteredApps,
                                key = { "${it.packageName}/${it.activityName}" }
                            ) { app ->
                                AppListItem(
                                    app = app,
                                    onClick = { onLaunchApp(app) },
                                    onLongClick = { viewModel.openAppMenu(app) },
                                    onToggleFavorite = { viewModel.toggleFavorite(app) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // App Long-Press Action Sheet
    selectedAppForMenu?.let { app ->
        AppContextMenu(
            app = app,
            onDismiss = viewModel::closeAppMenu,
            onLaunch = { onLaunchApp(app) },
            onToggleFavorite = { viewModel.toggleFavorite(app) },
            onAppInfo = { onAppInfo(app) },
            onUninstall = { onUninstallApp(app) },
            onOpenPlayStore = { onOpenPlayStore(app) },
            onShare = { onShareApp(app) }
        )
    }

    // Launcher Settings Sheet
    if (isSettingsOpen) {
        SettingsBottomSheet(
            columnsCount = columnsCount,
            onColumnsChange = viewModel::setColumnsCount,
            viewMode = viewMode,
            onViewModeChange = viewModel::setViewMode,
            showLabels = showLabels,
            onShowLabelsChange = viewModel::setShowLabels,
            showSystemApps = showSystemApps,
            onShowSystemAppsChange = viewModel::setShowSystemApps,
            onDismiss = viewModel::closeSettings
        )
    }
}

@Composable
private fun TabRowSection(
    currentTab: LauncherTab,
    allAppsCount: Int,
    favoritesCount: Int,
    onTabSelect: (LauncherTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TabPill(
            title = stringResource(R.string.all_apps_title),
            count = allAppsCount,
            icon = Icons.Default.Apps,
            isSelected = currentTab == LauncherTab.ALL,
            onClick = { onTabSelect(LauncherTab.ALL) }
        )

        TabPill(
            title = stringResource(R.string.favorites_title),
            count = favoritesCount,
            icon = Icons.Default.Star,
            isSelected = currentTab == LauncherTab.FAVORITES,
            onClick = { onTabSelect(LauncherTab.FAVORITES) }
        )
    }
}

@Composable
private fun TabPill(
    title: String,
    count: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$title ($count)",
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun EmptyAppsState(
    query: String,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_apps_found),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        if (query.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "No apps matching \"$query\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.65f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onClearQuery)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Clear Search",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
