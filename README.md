# Golestan Launcher 🚀

A fast and feature-rich Android launcher named **Golestan Launcher**, built with
**Kotlin** and **Jetpack Compose**.

---

## ✨ Features

- **Full Application Drawer**:
    - Automatically queries and displays all installed applications on the Android device
      (supporting Android 8.0 up to Android 15+ / API 26-35).
    - High-performance icon and metadata loading in background coroutines.
    - Alphabetically sorted with locale-aware collation.

- **Instant Search**:
    - Real-time instant filtering as you type by app display name or package identifier.
    - Clear search button and keyboard dismissal actions.

- **One-Tap App Launching**:
    - Seamlessly launches any installed application with standard Android launch flags.
    - Fallback mechanisms for complex multi-activity packages.

- **Favorites & Pinning**:
    - Star / pin frequently used apps to the **Favorites** tab.
    - Quick tab switching between **All Apps** and **Favorites**.

- **App Context Menu (Long-Press)**:
    - Long-press any app item to open a Material 3 bottom sheet:
        - **Open App**: Direct launch.
        - **Pin / Unpin**: Manage favorites.
        - **App Info**: Direct jump to Android system application settings (permissions, storage,
          cache).
        - **Uninstall**: Prompt system package uninstallation (for non-system apps).
        - **Google Play Store**: Open the app's official Play Store listing.
        - **Share**: Share Google Play download link.

- **Customizable Grid & Layouts**:
    - **Grid View** with configurable columns (3, 4, 5, or 6 columns).
    - **List View** with package names, system app badges, and direct action icons.
    - Show / hide app labels underneath icons.
    - Include or exclude pre-installed system applications.

- **Live Package Monitoring**:
    - Dynamic `PackageChangeReceiver` that listens for `ACTION_PACKAGE_ADDED`,
      `ACTION_PACKAGE_REMOVED`, and `ACTION_PACKAGE_CHANGED`.
    - App drawer updates immediately when apps are installed or removed without needing to restart
      the launcher.

- **Wallpaper & System Bar Integration**:
    - Configured with `android:windowShowWallpaper="true"` and transparent system bars
      (`enableEdgeToEdge()`) so your device's wallpaper and themes show through naturally.