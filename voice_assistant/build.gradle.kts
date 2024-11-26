import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { stream ->
        localProperties.load(stream)
    }
}

val uni_sound_asr_tts_app_key: String =
    localProperties.getProperty("uni_sound_asr_tts_app_key").ifEmpty { "" }
val uni_sound_asr_tts_secret_key: String =
    localProperties.getProperty("uni_sound_asr_tts_secret_key").ifEmpty { "" }
val uni_sound_wakeup_app_key: String =
    localProperties.getProperty("uni_sound_wakeup_app_key").ifEmpty { "" }
val uni_sound_wakeup_secret_key: String =
    localProperties.getProperty("uni_sound_wakeup_secret_key").ifEmpty { "" }
val uni_sound_device_id: String =
    localProperties.getProperty("uni_sound_device_id").ifEmpty { "" }



android {
    namespace = "com.popkter.voice_assistant"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        buildConfigField("String", "UNI_SOUND_ASR_TTS_APP_KEY", uni_sound_asr_tts_app_key)
        buildConfigField("String", "UNI_SOUND_ASR_TTS_SECRET_KEY", uni_sound_asr_tts_secret_key)
        buildConfigField("String", "UNI_SOUND_WAKE_UP_APP_KEY", uni_sound_wakeup_app_key)
        buildConfigField("String", "UNI_SOUND_WAKE_UP_SECRET_KEY", uni_sound_wakeup_secret_key)
        buildConfigField("String", "UNI_SOUND_DEVICE_ID", uni_sound_device_id)


        consumerProguardFiles("consumer-rules.pro")
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
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.test.ext.junit)
//    androidTestImplementation(libs.espresso.core)
    implementation(libs.gson)
    implementation(libs.bundles.exoplayer)
}