plugins {
    alias(libs.plugins.emberfocus.android.library)
    alias(libs.plugins.emberfocus.android.room)
    alias(libs.plugins.emberfocus.koin)
}

android {
    namespace = "com.emberfocus.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.android)
}
