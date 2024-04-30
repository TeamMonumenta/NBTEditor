plugins {
    id("com.goncalomb.bukkit.java-conventions")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    implementation("org.apache.commons:commons-lang3:3.0")
}

description = "adapter_api"
version = rootProject.version
