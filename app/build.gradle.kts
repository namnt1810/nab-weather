import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.*
import java.io.FileInputStream

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
//    id("com.google.devtools.ksp") version "1.7.20-1.0.8"
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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    namespace = "vn.namnt.nabweather"

    signingConfigs {
        getByName("debug") {
            val fis = FileInputStream(rootProject.file("signing/keystoreDebug.properties"))
            val properties = Properties()
            properties.load(fis)

            storeFile = file(properties.getProperty("KEY_STORE"))
            storePassword = properties.getProperty("KEY_STORE_PASSWORD")
            keyAlias = properties.getProperty("KEY_STORE_ALIAS")
            keyPassword = properties.getProperty("KEY_STORE_ALIAS_PASSWORD")
        }

        create("release") {
            // By default, the release keystore will not add to source control. In case testing
            // release build on local, we will fallback to using the debug keystore instead
            var configFile = rootProject.file("signing/keystoreRelease.properties")
            if (!configFile.exists()) {
                configFile = project.rootProject.file("signing/keystoreDebug.properties")
            }

            val fis = FileInputStream(configFile)
            val properties = Properties()
            properties.load(fis)

            storeFile = file(properties.getProperty("KEY_STORE"))
            storePassword = properties.getProperty("KEY_STORE_PASSWORD")
            keyAlias = properties.getProperty("KEY_STORE_ALIAS")
            keyPassword = properties.getProperty("KEY_STORE_ALIAS_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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

    testOptions.unitTests {
        isIncludeAndroidResources = true
    }
}

dependencies {
    api(platform(project(":depconstraints")))
    kapt(platform(project(":depconstraints"))) // Dagger does not support KSP yet
//    ksp(platform(project(":depconstraints")))
    testApi(platform(project(":depconstraints")))
    androidTestApi(platform(project(":depconstraints")))

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

    implementation(Libs.LIFECYCLE_RUNTIME)
    implementation(Libs.VIEW_MODEL_KTX)

    implementation(Libs.ROOM)
    implementation(Libs.ROOM_KTX)
    kapt(Libs.ROOM_COMPILER)
//    ksp(Libs.ROOM_COMPILER)

    implementation(Libs.WORK_MANAGER)

    // 3rd-party
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

//    debugImplementation(Libs.LEAK_CANARY)

    // Instrumentation tests
    androidTestImplementation(Libs.COROUTINE_TEST)
    androidTestImplementation(Libs.ESPRESSO_CORE)
    androidTestImplementation(Libs.ANDROID_RUNNER)
    androidTestImplementation(Libs.ANDROID_RUNNER_EXT)
    androidTestImplementation(Libs.MOCK_WEB_SERVER)

    // Local unit tests
    testImplementation(Libs.COROUTINE_TEST)
    testImplementation(Libs.JUNIT)
    testImplementation(Libs.ROBOELECTRIC)
    testImplementation(Libs.MOCKITO_CORE)
    testImplementation(Libs.MOCKITO_KOTLIN)
    testImplementation(Libs.MOCKITO_INLINE)
    testImplementation(Libs.MOCK_WEB_SERVER)
}
