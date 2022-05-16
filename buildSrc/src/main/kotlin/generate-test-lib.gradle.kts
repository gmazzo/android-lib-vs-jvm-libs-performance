import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer

val baseName = project.name.capitalize()
val kind = providers.gradleProperty("kind")
    .orElse("android")
val classesCount = providers.gradleProperty("classesCount")
    .map(String::toInt)
    .orElse(200)

when (kind.get()) {
    "jvm" -> {
        apply(plugin = "kotlin")
    }
    "android" -> {
        apply(plugin = "android-library")
        apply(plugin = "kotlin-android")

        the<LibraryExtension>().compileSdk = 31
    }
    else -> error("Unknown kind: $kind")
}

val generateClasses = tasks.register("generateLibClasses") {
    val outputDir = layout.dir(provider { temporaryDir })
        .zip(classesCount) { dir, count -> dir.dir(count.toString()) }

    inputs.property("baseName", baseName)
    outputs.dir(outputDir)

    doLast {
        (1..classesCount.get()).forEach {
            outputDir.get().file("$baseName$it.kt").asFile.writeText(
                """
                package $baseName
                
                data class $baseName$it(
                    val prop1: String,
                    val prop2: String,
                    val prop3: String,
                )
            """.trimIndent()
            )
        }
    }
}

the<KotlinSourceSetContainer>().sourceSets.named("main").configure {
    kotlin.srcDir(generateClasses)
}
