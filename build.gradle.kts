import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("com.playmonumenta.gradle-config") version "1.+"
}

monumenta {
    name("NBTEditor")
    pluginProject(":nbteditor")
    paper(
        "com.goncalomb.bukkit.nbteditor.NBTEditor", BukkitPluginDescription.PluginLoadOrder.POSTWORLD, "1.18.2",
        authors = listOf("goncalomb", "Team Monumenta"),
        depends = listOf("CommandAPI"),
        softDepends = listOf("MonumentaNetworkRelay")
    )

    versionAdapterApi("adapter_api", paper = "1.18.2") {
        dependencies {
            implementation("org.apache.commons:commons-lang3:3.0")
        }
    }
    versionAdapter("adapter_v1_18_R2", "1.18.2")
    versionAdapter("adapter_v1_19_R3", "1.19.4")
}
