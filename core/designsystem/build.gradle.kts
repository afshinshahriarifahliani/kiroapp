plugins {
    alias(libs.plugins.emberfocus.android.library)
    alias(libs.plugins.emberfocus.android.library.compose)
}

android {
    namespace = "com.emberfocus.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.ktx)
}
