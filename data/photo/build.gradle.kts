plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.knotworking.whereami.data.photo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}

dependencies {
    implementation(project(":domain:photo"))
    implementation(project(":core:network"))
    
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.datastore.preferences)
}
