plugins {
    id("java-platform")
}

val coroutines = "1.6.4"

val javaAnnotation = "1.3.2"
val annotation = "1.1.0"
val appcompat = "1.1.0"
val constraintLayout = "2.0.4"
val core = "1.2.0"
val dagger = "2.44.2"
val espresso = "3.1.1"
val guava = "31.1-android"
val gson = "2.8.6"
val junit = "4.13"
val lifecycle = "2.5.1"
val material = "1.2.1"
val mockito = "3.3.1"
val mockitoKotlin = "1.5.0"
val multidex = "2.0.1"
val okhttp = "3.10.0"
val retrofit = "2.9.0"
val room = "2.4.3"
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

        api("${Libs.LIFECYCLE_RUNTIME}:$lifecycle")
        api("${Libs.VIEW_MODEL_KTX}:$lifecycle")

        api("${Libs.ROOM}:$room")
        api("${Libs.ROOM_KTX}:$room")
        api("${Libs.ROOM_COMPILER}:$room")

        api("${Libs.WORK_MANAGER}:2.7.1")

        // 3rd-party
        api("${Libs.DAGGER}:$dagger")
        api("${Libs.DAGGER_ANDROID}:$dagger")
        api("${Libs.DAGGER_COMPILER}:$dagger")
        api("${Libs.DAGGER_PROCESSOR}:$dagger")

        api("${Libs.RETROFIT}:$retrofit")
        api("${Libs.RETROFIT_GSON_CONVERTER}:$retrofit")

        api("${Libs.OKHTTP_LOGGING_INTERCEPTOR}:4.10.0")

        api("${Libs.GSON}:2.8.6")

        // Test
        api("${Libs.ANDROID_RUNNER}:1.5.1")
        api("${Libs.ANDROID_RUNNER_EXT}:1.1.4")
        api("${Libs.ESPRESSO_CORE}:3.5.0")
        api("${Libs.ROBOELECTRIC}:4.9")
        api("${Libs.COROUTINE_TEST}:1.6.4")
        api("${Libs.JUNIT}:4.13.2")
        api("${Libs.MOCKITO_CORE}:4.9.0")
        api("${Libs.MOCKITO_KOTLIN}:4.1.0")
        api("${Libs.MOCKITO_INLINE}:4.9.0")
        api("${Libs.MOCK_WEB_SERVER}:4.10.0")
    }
}
