import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt.android)
}

val localProperties = Properties()
val localFile = rootProject.file("local.properties")
if (localFile.exists()) {
    localProperties.load(localFile.inputStream())
} else {
    throw GradleException("local.properties not found")
}

android {
    namespace = "com.hamric.core.network"
    compileSdk {
        version = release(35)
    }

    defaultConfig {
        minSdk = 30

        consumerProguardFiles("consumer-rules.pro")
    }

    defaultConfig {
        buildConfigField("String", "TMDB_API_BASE_URL", "\"https://api.themoviedb.org/3/\"")
    }

    buildTypes {
        debug{
            buildConfigField("String", "TMDB_API_KEY", "\"${localProperties.getProperty("TMDB_API_KEY_DEV") ?: error("TMDB_API_KEY_DEV not found in local.properties")}\"")
        }
        release{
            buildConfigField("String", "TMDB_API_KEY", "\"${localProperties.getProperty("TMDB_API_KEY_PROD") ?: error("TMDB_API_KEY_PROD not found in local.properties")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))

    //network
    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.squareup.okhttp3)
    implementation(libs.squareup.okhttp3.logging.interceptor)

    //gson serialization
    implementation(libs.google.code.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //hilt dagger di
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
}