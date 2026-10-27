import com.emberfocus.buildlogic.library
import com.emberfocus.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/**
 * Convention for feature modules: Android library + Compose + Koin + shared UI deps.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("emberfocus.android.library")
            pluginManager.apply("emberfocus.android.library.compose")
            pluginManager.apply("emberfocus.koin")

            val libs = libs
            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:data"))
                add("implementation", project(":core:common"))

                add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel-compose"))
                add("implementation", libs.library("androidx-navigation-compose"))
                add("implementation", libs.library("androidx-compose-material-icons-extended"))

                add("testImplementation", libs.library("junit"))
                add("testImplementation", libs.library("kotlinx-coroutines-test"))
                add("testImplementation", libs.library("turbine"))
            }
        }
    }
}
