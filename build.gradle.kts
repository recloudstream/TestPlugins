import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import com.lagradost.gradle.topplugin.TopPluginExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.recloudstream:gradle:-SNAPSHOT")
    }
}

plugins {
    id("com.android.library")
    kotlin("android")
}

apply(plugin = "com.lagradost.cloudstream3.gradle")

configure<CloudstreamExtension> {
    // This handles tracking compilation targets automatically
}
