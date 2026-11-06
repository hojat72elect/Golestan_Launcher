## Golestan Launcher

A minimal, experimental Android home-screen launcher built with **Expo** and **React Native**, featuring a custom native Kotlin module for querying and launching installed applications.

### Features

- **App grid** — all launchable installed apps displayed in a responsive 4-column grid
- **Instant search** — filter apps by name as you type
- **Native bridge** — a hand-written Kotlin `ReactContextBaseJavaModule` (no third-party launcher libraries)

## Tech Stack

| Layer     | Technology                                |
|-----------|-------------------------------------------|
| UI        | React Native 0.88.1, React 19.3, TypeScript |
| Framework | Expo SDK 58 (development builds)          |
| Native    | Kotlin (Android native module)            |

### How to run the project

The first build compiles the Kotlin module and may take a few minutes.
