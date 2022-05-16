dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "My Application"

include(":app")

val libsCount = providers.gradleProperty("libsCount")
    .map(String::toInt)
    .orElse(100)

(1..libsCount.get()).forEach {
    include(":libs:lib$it")

    val libDir = file("libs/lib$it")
    libDir.mkdirs()

    file("$libDir/build.gradle.kts")
        .writeText(
        """
        plugins {
            `generate-test-lib`
        }
        """.trimIndent())

    file("$libDir/src/main/AndroidManifest.xml")
        .apply { parentFile.mkdirs() }
        .writeText("<manifest package=\"lib$it\"/>")
}
