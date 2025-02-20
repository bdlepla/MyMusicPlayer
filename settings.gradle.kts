pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "MyMusicPlayer"
include(":app")

//gradle.ext.androidxMediaModulePrefix = 'media-'
//apply from: file("/home/bryan/dev/AndroidStudioProjects/media/core_settings.gradle")

include(":app:baselineprofile")
