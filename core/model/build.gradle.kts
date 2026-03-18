plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.knotworking.whereami.core.model"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}
