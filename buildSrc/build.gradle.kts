plugins {
    `kotlin-dsl`
}

dependencies {
    fun plugin(pluginId: String, version: String) =
        create("$pluginId:$pluginId.gradle.plugin:$version")

    implementation(plugin("com.android.library", version = "7.1.3"))
    implementation(plugin("org.jetbrains.kotlin.android", version = "1.5.30"))
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}
