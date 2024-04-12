// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("com.android.application") version "8.3.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.9.10" apply false
    //id ("dagger.hilt.android.plugin") version "2.40.1" apply false
    id ("com.google.protobuf") version "0.9.4" apply false
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    compilerOptions.freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true",
    )
}