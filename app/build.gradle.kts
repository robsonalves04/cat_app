plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.cat_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cat_app"
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
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation ("androidx.compose.material:material:1.6.8")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.activity:activity-compose:1.8.2")
    implementation ("androidx.compose.material:material-icons-extended:1.6.4")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.4")
    implementation ("io.insert-koin:koin-android:3.5.0")
    implementation ("io.insert-koin:koin-androidx-compose:3.5.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.accompanist:accompanist-placeholder-material:0.28.0")
    implementation("androidx.compose.foundation:foundation:1.6.0")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.accompanist:accompanist-pager:0.32.0")

    implementation(libs.androidx.foundation.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}