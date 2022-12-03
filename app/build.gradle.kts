import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = Configs.COMPILE_SDK

    defaultConfig {
        applicationId = Configs.APPLICATION_ID
        minSdk = Configs.MIN_SDK
        targetSdk = Configs.TARGET_SDK
        versionCode = Configs.VERSION_CODE
        versionName = Configs.VERSION_NAME

        vectorDrawables.useSupportLibrary = true
    }

    namespace = "vn.namnt.nabweather"

    signingConfigs {
        create("release")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            val apkName =
                "NAB_NamNT_Weather_${Configs.VERSION_CODE}_v${Configs.VERSION_NAME}_${Configs.BUILD_NUMBER}"
            android.applicationVariants.all {
                outputs.all {
                    if (this is BaseVariantOutputImpl && name == "release") {
                        outputFileName = "$apkName.apk"
                    }
                }

//                if (isMinifyEnabled) {
//                    assembleProvider?.get()?.doLast {
//                        val mappingFiles = mappingFileProvider.get().files
//                        for (file in mappingFiles) {
//                            if (file != null && file.exists()) {
//                                file.renameTo(File(file.parentFile, "$apkName-mapping.txt"))
//                            }
//                        }
//                    }
//                }
            }
        }
        getByName("debug") {
//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    lint {
        // Eliminates UnusedResources false positives for resources used in DataBinding layouts
        isCheckGeneratedSources = true
        // Running lint over the debug variant is enough
        isCheckReleaseBuilds = false
        // See lint.xml for rules configuration
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(platform(project(":depconstraints")))
    kapt(platform(project(":depconstraints")))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Java
    implementation(Libs.JAVA_ANNOTATION)
    implementation(Libs.GUAVA)

    // Kotlin
    implementation(Libs.KOTLIN_STDLIB)
    implementation(Libs.COROUTINES)

    // Android
    implementation(Libs.ANNOTATION)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.CONSTRAINT_LAYOUT)
    implementation(Libs.CORE_KTX)
    implementation(Libs.MATERIAL)
    implementation(Libs.MULTI_DEX)

    // 3rd-party
    // Dagger
    implementation(Libs.DAGGER)
    implementation(Libs.DAGGER_ANDROID)
    kapt(Libs.DAGGER_COMPILER)
    kapt(Libs.DAGGER_PROCESSOR)

    // Gson
    implementation(Libs.GSON)

//    debugImplementation(Libs.LEAK_CANARY)

    // Instrumentation tests
    androidTestImplementation(Libs.ESPRESSO_CORE)
    androidTestImplementation(Libs.ANDROID_RUNNER)

    // Local unit tests
    testImplementation(Libs.JUNIT)
}