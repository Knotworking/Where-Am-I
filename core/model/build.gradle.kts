plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.knotworking.whereami.core.model"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}
