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
    namespace = "com.knotworking.whereami.data.photo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        val flickrKey = localProps.getProperty("FLICKR_API_KEY") ?: ""
        buildConfigField("String", "FLICKR_API_KEY", "\"$flickrKey\"")
        val benHikesBaseUrl = localProps.getProperty("BENHIKES_BASE_URL") ?: ""
        buildConfigField("String", "BENHIKES_BASE_URL", "\"$benHikesBaseUrl\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain:photo"))
    implementation(project(":core:network"))
    implementation(project(":core:domain"))
    
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.okhttp)

    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testRuntimeOnly(libs.junit5.launcher)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
}
