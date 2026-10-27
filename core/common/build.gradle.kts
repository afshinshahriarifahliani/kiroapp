plugins {
    alias(libs.plugins.emberfocus.android.library)
    alias(libs.plugins.emberfocus.koin)
}

android {
    namespace = "com.emberfocus.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
