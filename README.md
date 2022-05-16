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
A Gradle module that applies `org.jetbrains.kotlin.jvm` plugin
```kotlin
plugins {
    id("com.android.library")
}
```
with all Android features off by adding these lines into root's `gradle.properties` file
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
./gradlew -Pkind=jvm assembleDebug --rerun-tasks
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
Command: [`./performTests.sh`](performTests.sh)

| number of libs | number of classes per lib | kind | command | took | build scan |
| --- | --- | --- | --- | --- | --- |
| 10 | 100 | android | ./gradlew -PlibsCount=10 -PclassesCount=100 -Pkind=android assembleDebug --rerun-tasks --scan | 7s | https://gradle.com/s/guyqxtl2jymrq |
| 10 | 100 | jvm | ./gradlew -PlibsCount=10 -PclassesCount=100 -Pkind=jvm assembleDebug --rerun-tasks --scan | 9s | https://gradle.com/s/l6kw4uoasnd2m |
| 10 | 300 | android | ./gradlew -PlibsCount=10 -PclassesCount=300 -Pkind=android assembleDebug --rerun-tasks --scan | 13s | https://gradle.com/s/dncdmsvt7f54k |
| 10 | 300 | jvm | ./gradlew -PlibsCount=10 -PclassesCount=300 -Pkind=jvm assembleDebug --rerun-tasks --scan | 13s | https://gradle.com/s/7o4r2yphwe5ha |
| 10 | 1000 | android | ./gradlew -PlibsCount=10 -PclassesCount=1000 -Pkind=android assembleDebug --rerun-tasks --scan | 21s | https://gradle.com/s/lv54zkjeyo55m |
| 10 | 1000 | jvm | ./gradlew -PlibsCount=10 -PclassesCount=1000 -Pkind=jvm assembleDebug --rerun-tasks --scan | 27s | https://gradle.com/s/aqxx7oy4d2cw4 |
| 50 | 100 | android | ./gradlew -PlibsCount=50 -PclassesCount=100 -Pkind=android assembleDebug --rerun-tasks --scan | 19s | https://gradle.com/s/gbqik5coxkcvs |
| 50 | 100 | jvm | ./gradlew -PlibsCount=50 -PclassesCount=100 -Pkind=jvm assembleDebug --rerun-tasks --scan | 18s | https://gradle.com/s/hk7kg2vpf2xl2 |
| 50 | 300 | android | ./gradlew -PlibsCount=50 -PclassesCount=300 -Pkind=android assembleDebug --rerun-tasks --scan | 33s | https://gradle.com/s/cm3xb7cf7esf6 |
| 50 | 300 | jvm | ./gradlew -PlibsCount=50 -PclassesCount=300 -Pkind=jvm assembleDebug --rerun-tasks --scan | 30s | https://gradle.com/s/n4uj2gkot4uvq |
| 50 | 1000 | android | ./gradlew -PlibsCount=50 -PclassesCount=1000 -Pkind=android assembleDebug --rerun-tasks --scan | 1m 14s | https://gradle.com/s/dhhvxknkhscs2 |
| 50 | 1000 | jvm | ./gradlew -PlibsCount=50 -PclassesCount=1000 -Pkind=jvm assembleDebug --rerun-tasks --scan | 1m 9s | https://gradle.com/s/jrohdwb5z2ake |
| 100 | 100 | android | ./gradlew -PlibsCount=100 -PclassesCount=100 -Pkind=android assembleDebug --rerun-tasks --scan | 29s | https://gradle.com/s/v2xvjyq5qas34 |
| 100 | 100 | jvm | ./gradlew -PlibsCount=100 -PclassesCount=100 -Pkind=jvm assembleDebug --rerun-tasks --scan | 28s | https://gradle.com/s/dubygrt3mrv22 |
| 100 | 300 | android | ./gradlew -PlibsCount=100 -PclassesCount=300 -Pkind=android assembleDebug --rerun-tasks --scan | 52s | https://gradle.com/s/jltrlbk4tguui |
| 100 | 300 | jvm | ./gradlew -PlibsCount=100 -PclassesCount=300 -Pkind=jvm assembleDebug --rerun-tasks --scan | 52s | https://gradle.com/s/dq3cvk2lshzmw |
| 100 | 1000 | android | ./gradlew -PlibsCount=100 -PclassesCount=1000 -Pkind=android assembleDebug --rerun-tasks --scan | 2m 16s | https://gradle.com/s/ksarreyis5xmi |
| 100 | 1000 | jvm | ./gradlew -PlibsCount=100 -PclassesCount=1000 -Pkind=jvm assembleDebug --rerun-tasks --scan | 2m 13s | https://gradle.com/s/i57obwy7dwwdm |

To conclude, there is evidence that either of the configurations performs faster than the other.
