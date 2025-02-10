plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}


android {
    namespace = "com.cookingassistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cookingassistant"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}



dependencies {
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation(libs.retrofit) // api into interface
    implementation (libs.androidx.security.crypto) // token encryption
    implementation(libs.converter.gson) // For JSON parsing with Gson
    implementation(libs.okhttp) // networking
    implementation(libs.logging.interceptor) // Optional: for logging HTTP requests
    implementation(libs.androidx.navigation.compose)
    implementation("com.github.jens-muenker:fuzzywuzzy-kotlin:1.0.0")
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.foundation.android)
    val nav_version = "2.8.2"
    implementation("androidx.compose.runtime:runtime:1.7.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.3")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.7.3")
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-rc01")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit) // Testing
    testImplementation(libs.mockk.mockk) // Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.byte.buddy)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}