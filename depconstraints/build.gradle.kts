plugins {
    id("java-platform")
}

val coroutines = "1.3.4"

val javaAnnotation = "1.3.2"
val annotation = "1.1.0"
val appcompat = "1.1.0"
val constraintLayout = "2.0.4"
val core = "1.2.0"
val dagger = "2.28.3"
val espresso = "3.1.1"
val guava = "31.1-android"
val gson = "2.8.6"
val junit = "4.13"
val material = "1.2.1"
val mockito = "3.3.1"
val mockitoKotlin = "1.5.0"
val multidex = "2.0.1"
val okhttp = "3.10.0"
val runner = "1.2.0"

dependencies {
    constraints {
        // Kotlin
        api("${Libs.KOTLIN_STDLIB}:${Versions.KOTLIN}")
        api("${Libs.COROUTINES}:$coroutines")

        // Java
        api("${Libs.JAVA_ANNOTATION}:$javaAnnotation")
        api("${Libs.GUAVA}:$guava")

        // Android
        api("${Libs.ANNOTATION}:$annotation")
        api("${Libs.APPCOMPAT}:$appcompat")
        api("${Libs.CONSTRAINT_LAYOUT}:$constraintLayout")
        api("${Libs.CORE_KTX}:$core")
        api("${Libs.MATERIAL}:$material")
        api("${Libs.MULTI_DEX}:$multidex")

        // 3rd-party
        api("${Libs.DAGGER}:$dagger")
        api("${Libs.DAGGER_ANDROID}:$dagger")
        api("${Libs.DAGGER_COMPILER}:$dagger")
        api("${Libs.DAGGER_PROCESSOR}:$dagger")

        api("${Libs.GSON}:$gson")

        // Test
        api("${Libs.ESPRESSO_CORE}:$espresso")
        api("${Libs.JUNIT}:$junit")
        api("${Libs.ANDROID_RUNNER}:$runner")
    }
}