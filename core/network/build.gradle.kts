import java.util.Properties

val localProps = Properties().also { props ->
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { props.load(it) }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.knotworking.whereami.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        
        val flickrKey = localProps.getProperty("FLICKR_API_KEY") ?: ""
        buildConfigField("String", "FLICKR_API_KEY", "\"$flickrKey\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
}
