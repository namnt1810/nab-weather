plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "vn.namnt.nabweather.testcommon"
    compileSdk = Configs.COMPILE_SDK

    defaultConfig {
        minSdk = Configs.MIN_SDK
        targetSdk = Configs.TARGET_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(platform(project(":depconstraints")))
    testApi(platform(project(":depconstraints")))

    implementation(project(":common"))
    implementation(project(":entity"))
    implementation(project(":data"))

    // Instrumentation tests
    api(Libs.ESPRESSO_CORE)
    api(Libs.ANDROID_RUNNER)
    api(Libs.ANDROID_RUNNER_EXT)

    // Local unit tests
    api(Libs.COROUTINE_TEST)
    api(Libs.JUNIT)
    api(Libs.ROBOELECTRIC)
    api(Libs.MOCKITO_CORE)
    api(Libs.MOCKITO_KOTLIN)
    api(Libs.MOCKITO_INLINE)
    api(Libs.MOCK_WEB_SERVER)
}