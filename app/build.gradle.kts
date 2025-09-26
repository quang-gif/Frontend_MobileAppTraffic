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
}

dependencies {
    implementation("com.google.android.material:material:1.8.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// OkHttp (nếu cần debug log)
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
// Gson
    implementation("com.google.code.gson:gson:2.10.1")
}