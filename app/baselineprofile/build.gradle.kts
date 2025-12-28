import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.bdlepla.android.mymusicplayer.baselineprofile"
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    defaultConfig {
        minSdk = 33
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"

}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
    useConnectedDevices = true
}

dependencies {
    implementation(libs.test.junit)
    implementation(libs.test.espresso)
    implementation(libs.uiautomator)
    implementation(libs.macro.benchmark)
}

//androidComponents {
//    onVariants { v ->
//        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
//        v.instrumentationRunnerArguments.put(
//            "targetAppId",
//            v.testedApks.map { artifactsLoader.load(it)?.applicationId }
//        )
//    }
//}