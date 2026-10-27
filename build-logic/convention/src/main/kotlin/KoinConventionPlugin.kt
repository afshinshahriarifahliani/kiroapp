import com.emberfocus.buildlogic.library
import com.emberfocus.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Wires Koin (BOM + core + android + compose) into a module.
 */
class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = libs
            dependencies {
                val bom = libs.library("koin-bom")
                add("implementation", platform(bom))
                add("implementation", libs.library("koin-core"))
                add("implementation", libs.library("koin-android"))
                add("implementation", libs.library("koin-androidx-compose"))
            }
        }
    }
}
