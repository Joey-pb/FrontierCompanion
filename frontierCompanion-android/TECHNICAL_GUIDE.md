# Technical Guide: Frontier Companion

---

## Overview
This document provides technical instructions for updating, maintaining, and deploying the Frontier Companion Android application.

### Tech Stack
- **Language**: Java / Kotlin
- **Architecture**: MVVM (ViewModel, Repository, DAO)
- **Database**: Room (SQLite) with pre-populated data
- **Networking**: Retrofit 2, OkHttp 3, GSON
- **Maps**: Google Maps SDK for Android
- **Services**: Firebase (Analytics, Cloud Messaging)
- **UI**: Data Binding, View Binding, Navigation Component, Material Design

---

## Maintenance

### 1. Database Management
The application uses a Room database that is initialized from a pre-populated asset.

- **Location**: `app/src/main/assets/database/prepopulated.db`
- **Schema**: Defined in `wgu.jbas127.frontiercompanion.data.entities`
- **Database Class**: `wgu.jbas127.frontiercompanion.data.database.AppDatabase`

**To update exhibit data:**
1. Modify the `prepopulated.db` SQLite file with new or updated data for Exhibits, Exhibit Panels, or Articles.
2. If the schema changes (e.g., adding a new field to an Entity):
    - Update the corresponding Entity class in Java.
    - Increment the `version` number in `AppDatabase.java`.
    - Room will recreate the database from the asset upon the next application launch.

### 2. API Keys and Secrets
The application relies on external services that require API keys. These are stored locally and should not be committed to version control.

**File**: `local.properties` (at the project root)

**Required entries**:
- `MAPS_API_KEY`: Your Google Maps SDK key.
- `FCMV_API_KEY`: API key for the Frontier Culture Museum backend.
- `BACKEND_BASE_URL`: The base URL for the backend search API (e.g., `https://frontier-companion-backend.onrender.com/`).

### 3. Dependency Updates
Dependencies and their versions are centralized in the Gradle Version Catalog.

- **File**: `gradle/libs.versions.toml`

**To update a library**:
1. Open `libs.versions.toml`.
2. Update the version number in the `[versions]` section.
3. Sync the project with Gradle files.

---

## Updating the App

### 1. Application Versioning
Versioning is controlled in the app-level build configuration.

- **File**: `app/build.gradle.kts`

**Steps**:
1. Increment `versionCode` (must be an integer, used by Google Play to identify newer versions).
2. Update `versionName` (a string visible to users, e.g., `"1.1"`).

### 2. SDK Configuration
- **compileSdk**: 36
- **minSdk**: 27 (Android 8.1)
- **targetSdk**: 36

---

## Deployment

### 1. Building the Application
Use Gradle to build the application for distribution.

- **Debug Build**: `./gradlew assembleDebug` (generates APK in `app/build/outputs/apk/debug/`)
- **Release Build**: `./gradlew assembleRelease` (generates APK in `app/build/outputs/apk/release/`)

### 2. Release Signing
To deploy to the Google Play Store, the app must be signed with a release key.
1. Generate a keystore file (`.jks` or `.keystore`).
2. Use the **Generate Signed Bundle / APK** wizard in Android Studio or configure the `signingConfigs` in `app/build.gradle.kts`.

### 3. Google Services
The application requires a `google-services.json` file to be present (typically in the `app/` or `app/src/` directory) for Firebase services to function correctly. Ensure this file is updated if you change Firebase projects.

---

## Testing

### 1. Unit Tests
Located in `app/src/test/java`. These tests focus on business logic and repository functions.
- **Command**: `./gradlew test`

### 2. Instrumentation Tests
Located in `app/src/androidTest/java`. These tests run on a device or emulator and verify database operations and UI behavior.
- **Command**: `./gradlew connectedAndroidTest`

---

## Technical Debt and TODOs
Key areas identified for future maintenance (search for `TODO` in the codebase):
- **Maps**: Implementation of full route drawing logic.
- **Exhibits**: Implementation of Action Panels, "View Full Article", and "Locate on Map" intents.
- **Media**: Integration of Glide for optimized image loading.
- **Home**: Completion of the Home screen layout and content.
