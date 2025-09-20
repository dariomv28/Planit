// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.11.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id ("com.google.dagger.hilt.android") version "2.57.1" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}