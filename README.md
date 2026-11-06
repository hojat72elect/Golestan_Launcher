# Nova Golestan Launcher 🚀

A modern, fast, and feature-rich native Android launcher inspired by **Nova Launcher**, built with **Kotlin** and **Jetpack Compose (Material 3)**.

---

## ✨ Features

- **Full Application Drawer**:
  - Automatically queries and displays all installed applications on the Android device (supporting Android 8.0 up to Android 15+ / API 26-35).
  - High-performance icon and metadata loading in background coroutines.
  - Alphabetically sorted with locale-aware collation.
  
- **Instant Search**:
  - Nova-style floating search bar.
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
    - **App Info**: Direct jump to Android system application settings (permissions, storage, cache).
    - **Uninstall**: Prompt system package uninstallation (for non-system apps).
    - **Google Play Store**: Open the app's official Play Store listing.
    - **Share**: Share Google Play download link.

- **Customizable Grid & Layouts**:
  - **Grid View** with configurable columns (3, 4, 5, or 6 columns).
  - **List View** with package names, system app badges, and direct action icons.
  - Show / hide app labels underneath icons.
  - Include or exclude pre-installed system applications.

- **Live Package Monitoring**:
  - Dynamic `PackageChangeReceiver` that listens for `ACTION_PACKAGE_ADDED`, `ACTION_PACKAGE_REMOVED`, and `ACTION_PACKAGE_CHANGED`.
  - App drawer updates immediately when apps are installed or removed without needing to restart the launcher.

- **Wallpaper & System Bar Integration**:
  - Configured with `android:windowShowWallpaper="true"` and transparent system bars (`enableEdgeToEdge()`) so your device's wallpaper and themes show through naturally.

---

## 📁 Project Structure

```
new_golestan/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/launcher/golestan/
│           │   ├── MainActivity.kt                # Launcher Activity with edge-to-edge & back handling
│           │   ├── data/
│           │   │   ├── AppPreferences.kt          # SharedPreferences for favorites & layout settings
│           │   │   └── AppRepository.kt           # PackageManager querying & coroutine loading
│           │   ├── model/
│           │   │   └── AppModel.kt                # App data model & enum states
│           │   ├── receiver/
│           │   │   └── PackageChangeReceiver.kt   # Live app install/uninstall broadcaster
│           │   ├── ui/
│           │   │   ├── components/
│           │   │   │   ├── AppContextMenu.kt      # Long-press action sheet
│           │   │   │   ├── AppGridItem.kt         # Grid item with icon, label shadow & favorite star
│           │   │   │   ├── AppIconView.kt         # Compose renderer for Android Drawables
│           │   │   │   ├── AppListItem.kt         # List layout item with details
│           │   │   │   ├── HomeClockWidget.kt     # Live digital clock & date header
│           │   │   │   ├── SearchBarComponent.kt  # Nova-style floating search bar
│           │   │   │   └── SettingsBottomSheet.kt # Columns, view mode, system app settings
│           │   │   ├── screens/
│           │   │   │   └── LauncherScreen.kt      # Main launcher layout & tab coordinator
│           │   │   └── theme/
│           │   │       ├── Color.kt
│           │   │       ├── Theme.kt
│           │   │       └── Type.kt
│           │   └── viewmodel/
│           │       └── LauncherViewModel.kt       # StateFlow management & search logic
│           └── res/
│               ├── drawable/                      # Vector assets & icons
│               ├── mipmap-anydpi-v26/             # Launcher adaptive icon
│               └── values/                        # Strings, colors, styles
├── gradle/
│   ├── libs.versions.toml                         # Version catalog (AGP 8.5+, Kotlin 2.0+, Compose)
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

## 🛠️ Requirements & Setup

### Prerequisites
- **Android Studio** (Koala / Ladybug or newer recommended)
- **JDK 17** or higher
- **Android SDK**:
  - `compileSdk`: 35 (Android 15)
  - `minSdk`: 26 (Android 8.0 Oreo)
  - `targetSdk`: 35

### Open in Android Studio
1. Launch **Android Studio**.
2. Select **Open** and browse to this directory: `d:\Dev\Repositories\new_golestan`.
3. Allow Android Studio to sync Gradle dependencies.

---

## 🚀 Building & Running

### Option 1: Run from Android Studio
1. Connect an Android phone via USB (with **USB Debugging** enabled) or start an Android Emulator.
2. Select `app` in the run configuration dropdown.
3. Click the green **Run (▶)** button (or press `Shift + F10`).

### Option 2: Build via Command Line
```powershell
# Build Debug APK
.\gradlew.bat assembleDebug

# Install on connected device/emulator
.\gradlew.bat installDebug
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 Setting as Default Launcher

1. On your Android phone, open **Settings**.
2. Go to **Apps** > **Default apps** > **Home app** (or search for *"Default home app"* in Settings).
3. Select **Nova Golestan**.
4. Pressing the physical or gesture Home button will now always take you to **Nova Golestan**!
