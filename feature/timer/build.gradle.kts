plugins {
    alias(libs.plugins.emberfocus.android.feature)
}

android {
    namespace = "com.emberfocus.feature.timer"
}

dependencies {
    implementation(projects.core.datastore)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.common)
}
