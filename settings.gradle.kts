rootProject.name = "nbteditor"
include(":adapter_api")
include(":adapter_v1_18_R2")
include(":adapter_v1_19_R3")
include(":adapter_v1_20_R3")
include(":nbteditor")
project(":nbteditor").projectDir = file("plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.playmonumenta.com/releases")
    }
}
