plugins {
  kotlin("multiplatform") version "1.7.0"
}

group = "com.xemantic.music"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val log4jVersion = "2.17.2"

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "15"
    }
    withJava()
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }
  js(IR) {
    browser {
      commonWebpackConfig {
        cssSupport.enabled = true
      }
    }
  }
  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux" -> linuxX64("native")
    isMingwX64 -> mingwX64("native")
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val jvmMain by getting {
      dependencies {
        implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")
      }
    }
    val jsMain by getting
    val jsTest by getting
    val nativeMain by getting
    val nativeTest by getting
  }

}
