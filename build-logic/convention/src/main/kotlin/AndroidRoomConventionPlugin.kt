import androidx.room.gradle.RoomExtension
import com.emberfocus.buildlogic.library
import com.emberfocus.buildlogic.libs
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidx.room")
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            val libs = libs
            dependencies {
                add("implementation", libs.library("room-runtime"))
                add("implementation", libs.library("room-ktx"))
                add("ksp", libs.library("room-compiler"))
            }
        }
    }
}
