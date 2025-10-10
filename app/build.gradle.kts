import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.frontend_mobileapptraffic"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.frontend_mobileapptraffic"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
//        buildConfigField("String", "BASE_URL", "\"http://192.168.0.148:8080/\"")
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
// OkHttp (nếu cần debug log)
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
// Gson
    implementation("com.google.code.gson:gson:2.10.1")
}