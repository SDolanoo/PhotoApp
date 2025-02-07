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
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.photoapp"
        minSdk = 27
        targetSdk = 34
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

val camerax_version = "1.2.2"

val nav_version = "2.8.5"

val room_version = "2.6.1"

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Dagger - Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //Apache Poi for exporting room as excel
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")

    // Google AI SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0") // Use the latest version
    // Kotlin Coroutines (if not already included)
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Use the latest version

    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    implementation("androidx.room:room-runtime:$room_version")
    implementation(libs.firebase.crashlytics.buildtools)
    kapt("androidx.room:room-compiler:$room_version")
    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation("androidx.compose.runtime:runtime-livedata:1.7.6")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

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