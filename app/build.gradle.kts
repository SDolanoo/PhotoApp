import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.internal.impldep.org.bouncycastle.util.Properties
import java.io.FileInputStream


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("org.jetbrains.kotlin.kapt") // using ksp instead of kapt
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.1.0"
}

val geminiApikey = gradleLocalProperties(rootDir, providers)
    .getProperty("geminiKey", "")

android {
    namespace = "com.example.photoapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.photoapp"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue(
            "string",
            "geminiKey",
            "\"" + geminiApikey + "\""
        )
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Dagger - Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    implementation(libs.poi)
    //noinspection UseTomlInstead
    implementation("org.apache.poi:poi-ooxml:5.2.5")

    // Google AI SDK for Android
    implementation(libs.generativeai) // Use the latest version
    // Kotlin Coroutines (if not already included)
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Use the latest version

    implementation(libs.androidx.navigation.compose)

    //noinspection GradleDependency
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    implementation(libs.androidx.room.runtime)
    implementation(libs.firebase.crashlytics.buildtools)
    kapt(libs.androidx.room.compiler)
    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    // optional - Test helpers
    testImplementation(libs.androidx.room.testing)

    //noinspection UseTomlInstead
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation(libs.androidx.runtime.livedata)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}

kapt {
    correctErrorTypes = true
}