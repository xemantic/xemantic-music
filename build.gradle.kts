plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.dokka)
  alias(libs.plugins.gradle.versions.plugin)
  `maven-publish`
}

repositories {
  mavenCentral()
}

kotlin {

  jvmToolchain(libs.versions.jvmTarget.get().toInt())

  jvm {
    compilations {
      all {
        kotlinOptions {
          jvmTarget = libs.versions.jvmTarget.get()
        }
      }
    }
  }

  js(IR) {
    browser {}
  }

  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  @Suppress("UNUSED_VARIABLE")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64()
    hostOs == "Linux" -> linuxX64()
    isMingwX64 -> mingwX64()
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  sourceSets {

    all {
      languageSettings {
        languageVersion = libs.versions.kotlinLanguageVersion.get()
        apiVersion = libs.versions.kotlinLanguageVersion.get()
      }
    }

    commonMain {
      dependencies {
        api(libs.kotlin.coroutines)
        api(libs.kotlin.datetime)
        implementation(libs.kotlin.logging)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlin.coroutines.test)
        implementation(libs.kotest.assertions.core)
      }
    }

    jvmTest {
      dependencies {
        runtimeOnly(libs.log4j.slf4j2)
        runtimeOnly(libs.log4j.core)
        runtimeOnly(libs.jackson.databind)
        runtimeOnly(libs.jackson.json)
      }
    }

  }

}

tasks {

  dependencyUpdates {
    gradleReleaseChannel = "current"
    rejectVersionIf {
      isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
  }

}

private val nonStableKeywords = listOf("alpha", "beta", "rc")

fun isNonStable(
  version: String
): Boolean = nonStableKeywords.any {
  version.lowercase().contains(it)
}
