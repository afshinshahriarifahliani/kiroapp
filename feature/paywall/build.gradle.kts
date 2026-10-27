plugins {
    alias(libs.plugins.emberfocus.android.feature)
}

android {
    namespace = "com.emberfocus.feature.paywall"
}

dependencies {
    implementation(projects.core.billing)
}
