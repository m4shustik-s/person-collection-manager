plugins {
    application
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "2.1.20"
}

application{
    mainClass.set("client.ClientKt")
}
group = "m4shustik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.google.code.gson:gson:2.10.1")
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