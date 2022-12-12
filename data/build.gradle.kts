plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "vn.namnt.nabweather.data"
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

    testOptions.unitTests {
        isIncludeAndroidResources = true
    }
}

dependencies {
    api(platform(project(":depconstraints")))
    testImplementation(project(mapOf("path" to ":testcommon")))
    kapt(platform(project(":depconstraints"))) // Dagger does not support KSP yet
    testApi(platform(project(":depconstraints")))

    implementation(project(":common"))
    implementation(project(":entity"))

    // Room
    implementation(Libs.ROOM)
    implementation(Libs.ROOM_KTX)
    kapt(Libs.ROOM_COMPILER)

    // Dagger
    implementation(Libs.DAGGER)
    implementation(Libs.DAGGER_ANDROID)
    kapt(Libs.DAGGER_COMPILER)
    kapt(Libs.DAGGER_PROCESSOR)

    // Gson
    implementation(Libs.GSON)

    // Retrofit
    implementation(Libs.RETROFIT)
    implementation(Libs.RETROFIT_GSON_CONVERTER)

    // Logging interceptor
    implementation(Libs.OKHTTP_LOGGING_INTERCEPTOR)

    androidTestImplementation(project(":testcommon"))
    testImplementation(project(":testcommon"))
}
