Sample test project to research if there is actually a **performance impact** between
1) "pure kotlin" modules (JVM platform modules)
2) "class only" Android library modules (turning all Android features off, like `BuildConfig` or `Resources`)

## Test Scenario
We have an `app` Android monolith which is composed by `200` libraries.
Each library contains only Kotlin `500` generated `data class`es.

### Hypothesis
"class only" Android library has no significant performance impact (when building a single variant) vs a regular jvm module

### Definitions
#### "class only" Android library
We turn all Android features off by adding these lines into `gradle.properties` file
```properties
android.nonTransitiveRClass=true
android.defaults.buildfeatures.aidl=false
android.defaults.buildfeatures.buildconfig=false
android.defaults.buildfeatures.renderscript=false
android.defaults.buildfeatures.resvalues=false
android.defaults.buildfeatures.shaders=false
android.defaults.buildfeatures.viewbinding=false
android.library.defaults.buildfeatures.androidresources=false
```

#### "pure kotlin" or "regular jvm" module
A Gradle module that applies `org.jetbrains.kotlin.jvm` plugin
```kotlin
plugins {
    kotlin("jvm")
}
```

### Measuring
We use the command for "class only" Android libs:
```sh
./gradlew -Pkind=android assembleDebug --rerun-tasks
```
And the command for "pure kotlin" JVM libs:
```sh
./gradlew -Pkind=kotlin assembleDebug --rerun-tasks
```
The [`buildSrc` ad-hoc plugin](buildSrc/src/main/kotlin/generate-test-lib.gradle.kts) will take care of read the `kind` property and perform a
minimum setup to make both approaches to work

### Environment
```
MacBook Pro (16-inch, 2021)
Chip Apple M1 Max
Memory 32 GB
openjdk version "11.0.13" 2021-10-19 LTS
OpenJDK Runtime Environment Zulu11.52+13-CA (build 11.0.13+8-LTS)
OpenJDK 64-Bit Server VM Zulu11.52+13-CA (build 11.0.13+8-LTS, mixed mode)
```

### Results
Run the command 3 times on a row for every type (to avoid any initial deviation) and took the last scan
| command | took | build scan |
| --- | --- | --- |
| `./gradlew -Pkind=android assembleDebug --rerun-tasks --scan` | 34s | https://gradle.com/s/fwxzk2sbjsfko |
| `./gradlew -Pkind=kotlin assembleDebug --rerun-tasks --scan` | 43s | https://gradle.com/s/svt4epv2vawty |

The results seems to be concluded. Pure Android libs seems to be **even faster than pure JVM ones**. 

I didn't dig deeper (yet) on the reason, but it's possible it's related to the extra Artifact Transform 
required to feed from a JAR, when the AGP plugin provides that already with a secondary outgoing variant.
