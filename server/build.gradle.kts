plugins {
    alias(libs.plugins.kotlinJvm)
    application
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("com.sunday.remotesample.server.ServerKt")
}

dependencies {
    implementation(project(":remote-documents"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    runtimeOnly(libs.logback.classic)
}
