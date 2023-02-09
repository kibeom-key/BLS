# Android Convention Plugins

## Build logic in a multi-repo setup sample
- Source : [docs.gradle.org](https://docs.gradle.org/current/samples/sample_publishing_convention_plugins.html) (Kotlin DSL)

## Modular Architecture
- Reference : [Now in Android](https://github.com/android/nowinandroid)

## NIA's build-logic (with Version Catalog)
- Organizing build logic
    <pre>
    ├── convention
    │   ├── build.gradle.kts
    │   └── src
    │       └── main
    │           └── kotlin
    │               └── AndroidXXXXXPlugin.kt
    │               └── AndroidYYYYYPlugin.kt
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    └── settings.gradle.kts
</pre>

- settings.gradle.kts
    ~~~kotlin
    dependencyResolutionManagement {
        repositories {
            google()
            mavenCentral()
        }
        // Version Catalog
        versionCatalogs {
            create("libs") {
                from(files("../gradle/libs.versions.toml"))
            }
        }
        rootProject.name = "build-logic"
        include(":convention")
    }
    ~~~

- build.gradle.kts
    ~~~kotlin
    plugins {
        `kotlin-dsl`
    }

    group = "com.google.samples.apps.nowinandroid.buildlogic"

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        compileOnly(libs.android.gradlePlugin)
        compileOnly(libs.kotlin.gradlePlugin)
        compileOnly(libs.ksp.gradlePlugin)
    }

    gradlePlugin {
        plugins {
            register("androidApplicationCompose") {
                id = "nowinandroid.android.application.compose"
                implementationClass = "AndroidApplicationComposeConventionPlugin"
            }

            // ...
        }
    }
    ~~~

