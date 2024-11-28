import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { stream->
        localProperties.load(stream)
    }
}

val deepSeekApiKey: String = localProperties.getProperty("deep_seek_api_key").ifEmpty { "" }

android {
    namespace = "com.popkter.collector"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.popkter.collector"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "DEEP_SEEK_API_KEY", deepSeekApiKey)

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
        buildConfig = true
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += arrayOf("/META-INF/{AL2.0,LGPL2.1}","META-INF/INDEX.LIST")
        }

    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.gson)
//    implementation(libs.lottie)
    implementation (libs.lottie.compose)
    implementation(libs.compose.coil)
    implementation(libs.coil.network.okhttp) // Only available on Android/JVM.

    implementation(libs.shapeView)

//    testImplementation(libs.junit)
//    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.plugins)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.websockets)
    implementation(libs.gson)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.coroutines.android)

    implementation(libs.ktor.client.logging)

    implementation(libs.ktor.client.serialization.json)
    implementation(libs.jlayer)
    implementation(project(":network"))
    implementation(project(":media"))
    implementation(project(":common"))
    implementation(project(":voice_assistant"))
}