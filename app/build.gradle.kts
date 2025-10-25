plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.happyplant"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.happyplant"
        minSdk = 27
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //  Firebase BoM (maneja versiones automáticamente)
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    //  Firebase Analytics (opcional, para métricas)
    implementation("com.google.firebase:firebase-analytics")

    //  Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    //  Firebase Auth (solo si usarás login/registro)
    implementation("com.google.firebase:firebase-auth")

    // Firebase Messaging (notificaciones)
    implementation("com.google.firebase:firebase-messaging")

    // 🔹 Google Play Services - Ubicación (para GPS)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    //  MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}