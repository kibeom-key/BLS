// Define conventions for service projects this organization.
// Service projects need to use the organization's Java conventions and pass some additional checks

plugins {
    id("com.myorg.java-conventions")
}

val integrationTest by sourceSets.creating

configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

val integrationTestTask = tasks.register<Test>("integrationTest") {
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath

    shouldRunAfter(tasks.test)
}

dependencies {
    "integrationTestImplementation"(project)
}

// The organization requires additional documentation in the README for this project
val readmeCheck by tasks.registering(com.example.ReadmeVerificationTask::class) {
    readme.set(layout.projectDirectory.file("README.md"))
    readmePatterns.set(listOf("^## Service API$"))
}

tasks.check { dependsOn(integrationTestTask, readmeCheck) }
