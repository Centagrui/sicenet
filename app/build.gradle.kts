plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.sicenet"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.sicenet"
        minSdk = 35
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    dependencies {
        // ... tus dependencias actuales ...

        // Retrofit para conexión SOAP
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-scalars:2.9.0") // Para recibir el XML como String

        // Navegación entre pantallas
        implementation("androidx.navigation:navigation-compose:2.7.7")

        // ViewModel para Compose
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

        // Retrofit: Para la conexión con el servidor SOAP de SICENET
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        // Scalars: Necesario para enviar y recibir el XML como String
        implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

        // ViewModel: Para que tu SicenetViewModel funcione con Compose
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

        // Navigation: Para moverte de la LoginScreen a la ProfileScreen
        implementation("androidx.navigation:navigation-compose:2.7.7")

        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    }
}