plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt.android)
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.techtestuserapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.techtestuserapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx.v1131)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx) // For viewModels delegate
    implementation(libs.androidx.fragment.ktx)

    // --- Lifecycle, Coroutines, Flow ---
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx) // For repeatOnLifecycle
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // --- Hilt (Dependency Injection) ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // --- Retrofit (Network) ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor) // For logging

    // --- Room (Database) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // For coroutines support
    kapt(libs.androidx.room.compiler)

    // --- UI ---
    implementation(libs.androidx.swiperefreshlayout) // Pull-to-refresh
    implementation(libs.shimmer) // Skeleton
    implementation(libs.coil) // Image loading
    implementation(libs.mpandroidchart) // For Chart

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
}

kapt {
    correctErrorTypes = true
}
