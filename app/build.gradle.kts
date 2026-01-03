plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "wgu.jbas127.frontiercompanion"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "wgu.jbas127.frontiercompanion"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Export RoomDb schema
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    // Room DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.play.services.maps)
    annotationProcessor(libs.androidx.room.compiler)

    // GSON
    implementation(libs.gson)

    // Mockito
    testImplementation(libs.mockito)

    // Core testing
    androidTestImplementation(libs.core.testing)
    testImplementation(libs.core.testing)

    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)

    // Espresso
    androidTestImplementation(libs.espresso.contrib)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}