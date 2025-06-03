plugins {
    application
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "2.1.20"
}

group = "m4shustik"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "client.ClientKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it)}
    })
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}