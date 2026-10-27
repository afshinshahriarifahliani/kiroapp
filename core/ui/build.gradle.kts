plugins {
    alias(libs.plugins.emberfocus.android.library)
    alias(libs.plugins.emberfocus.android.library.compose)
}

android {
    namespace = "com.emberfocus.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
