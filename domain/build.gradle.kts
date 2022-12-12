plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "vn.namnt.nabweather.domain"
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
    kapt(platform(project(":depconstraints"))) // Dagger does not support KSP yet
    testApi(platform(project(":depconstraints")))

    implementation(project(":common"))
    implementation(project(":entity"))
    implementation(project(":data"))

    implementation(Libs.DAGGER)
    implementation(Libs.DAGGER_ANDROID)
    kapt(Libs.DAGGER_COMPILER)
    kapt(Libs.DAGGER_PROCESSOR)

    testImplementation(project(":testcommon"))
}
