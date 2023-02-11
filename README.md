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
    │               └── AndroidHiltConventionPlugin.kt
    │               └── AndroidApplicationComposeConventionPlugin.kt
    │               └── ...
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
            register("androidHilt") {
                id = "nowinandroid.android.hilt"
                implementationClass = "AndroidHiltConventionPlugin"
            }

            // ...
        }
    }
    ~~~

## Custom Plugin Sample
- AndroidHiltConventionPlugin
    ~~~kotlin
    class AndroidHiltConventionPlugin : Plugin<Project> {
        override fun apply(target: Project) {
              with(target) {
                with(pluginManager) {
                    apply("dagger.hilt.android.plugin")
                    apply("org.jetbrains.kotlin.kapt")
                }
            }
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            "implementation"(libs.findLibrary("hilt.android").get())
            "kapt"(libs.findLibrary("hilt.compiler").get())
            "kaptAndroidTest"(libs.findLibrary("hilt.compiler").get())
        }
    }
    ~~~
- libs.versions.toml
    ~~~kotlin
    [versions]
    hilt = "2.44.2"

    [libraries]
    hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
    hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }

    [plugins]
    hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
    ~~~

- settings.gradle.kts (project)
    ~~~kotlin
    pluginManagement {
        includeBuild("build-logic")
        ...
    }
    ~~~
- build.gradle.kts (app)
    ~~~kotlin
    plugins {
        id("nowinandroid.android.hilt")
    }
    ~~~

## Reference site
- [https://velog.io/@vov3616/Gradle-3.-Custom-Plugin-%EB%A7%8C%EB%93%A4%EA%B8%B0](https://velog.io/@vov3616/Gradle-3.-Custom-Plugin-%EB%A7%8C%EB%93%A4%EA%B8%B0)
- [https://docs.gradle.org/current/userguide/custom_plugins.html#sec:packaging_a_plugin](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:packaging_a_plugin)