pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "EmberFocus"

include(":app")
include(":core:designsystem")
include(":core:ui")
include(":core:common")
include(":core:model")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:billing")
include(":feature:timer")
include(":feature:habits")
include(":feature:stats")
include(":feature:settings")
include(":feature:paywall")
