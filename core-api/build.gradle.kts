import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
}

val localProperties = getProperties("local.properties")
val gradleProperties = getProperties("gradle.properties")

val apiKey = getValue("OpenExchangeApiKey")
val apiUrl = getValue("OpenExchangeApiUrl")

fun getProperties(name: String): Properties {
    return Properties().apply {
        load(project.rootProject.file(name).inputStream())
    }
}

fun getValue(field: String): String {
    return localProperties.getProperty(field) ?: gradleProperties.getProperty(field) ?: ""
}

android {
    namespace = "com.stefang.app.core.api"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "com.stefang.app.core.testing.HiltTestRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "OPEN_EXCHANGE_API_KEY", apiKey)
        buildConfigField("String", "OPEN_EXCHANGE_API_URL", apiUrl)
    }

    buildFeatures {
        aidl = false
        buildConfig = true
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = rootProject.extra["javaVersion"] as JavaVersion
        targetCompatibility = rootProject.extra["javaVersion"] as JavaVersion
    }

    kotlinOptions {
        jvmTarget = rootProject.extra["kotlinJvmTarget"] as String
    }
}

dependencies {
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
}
