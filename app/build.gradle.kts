plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vibebell.hourly"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vibebell.hourly"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
