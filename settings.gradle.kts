rootProject.name = "nbteditor"
include(":adapter_api")
include(":adapter_v1_16_R3")
include(":adapter_v1_18_R2")
include(":nbteditor")
project(":nbteditor").projectDir = file("plugin")

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://papermc.io/repo/repository/maven-public/")
  }
}
