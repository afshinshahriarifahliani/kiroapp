plugins {
    alias(libs.plugins.emberfocus.android.library)
    alias(libs.plugins.emberfocus.koin)
}

android {
    namespace = "com.emberfocus.core.billing"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.datastore)
    implementation(libs.billing.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
