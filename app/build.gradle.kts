//import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
    //id("kotlin-kapt") apply false
    //id("dagger.hilt.android.plugin") apply false
}

android {
    compileSdk = 33

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    defaultConfig {
        //applicationId("com.bdlepla.android.mymusicplayer")
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        compileSdkPreview = "UpsideDownCake"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
//    fun Packaging.() {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//            excludes += "DebugProbesKt.bin"
//        }
//    }
    namespace = "com.bdlepla.android.mymusicplayer"
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0-rc01")

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }

    implementation("androidx.activity:activity-compose:1.8.0-alpha07")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.material3:material3:1.2.0-alpha06")
    implementation("androidx.compose.ui:ui:1.6.0-alpha04")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.core:core-ktx:1.12.0-rc01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.media3:media3-ui:1.1.1")
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-session:1.1.1")
    //implementation project(":media-lib-ui")
    //implementation project(":media-lib-exoplayer")
    //implementation project(":media-lib-session")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation("com.google.android.material:material:1.11.0-alpha02")
    implementation("com.google.dagger:hilt-android:2.47")
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0-alpha04")

    testImplementation("junit:junit:4.13.2")

    //kapt "com.google.dagger:hilt-compiler:2.42"
}