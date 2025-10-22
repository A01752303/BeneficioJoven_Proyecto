plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.govAtizapan.beneficiojoven"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.govAtizapan.beneficiojoven"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.benchmark.traceprocessor)
    implementation(libs.androidx.foundation)
    implementation(libs.ui.graphics)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.material3)
    implementation(libs.play.services.auth)
    implementation(libs.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.accompanist.pager)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.material.icons.extended.android)
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose.v6610)
    // Firebase BoM (Bill of Materials) - maneja las versiones por ti
    implementation(libs.firebase.bom)
    implementation(libs.google.firebase.auth)
    // Google Sign-In
    implementation(libs.play.services.identity.v1810)
    // Facebook Login
    implementation(libs.facebook.login)
    implementation(libs.androidx.lifecycle.runtime.compose)
    //Tadeo
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation("androidx.compose.material3:material3:1.2.1") // o 1.3.x estable
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")


    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson.core)

    //Shaiel
// Dependencias para Google Maps y Places
    implementation(libs.google.maps.android.compose) // Para MarkerState y ComposeMap
    implementation(libs.play.services.maps) // Para CameraPosition
    implementation(libs.play.services.location) // Para LocationServices
    implementation("com.google.android.libraries.places:places:3.5.0")


    // Accompanist Permissions para manejar permisos
    implementation(libs.accompanist.permissions) // Reemplazado por este estilo
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    //Jan
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("io.coil-kt:coil-svg:2.6.0")

    implementation("androidx.security:security-crypto-ktx:1.1.0")
}

