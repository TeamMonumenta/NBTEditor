import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("com.playmonumenta.gradle-config") version "2.1+"
}

val commons = libs.commons

monumenta {
    name("NBTEditor")
    pluginProject(":nbteditor")
    paper(
        "com.goncalomb.bukkit.nbteditor.NBTEditor", BukkitPluginDescription.PluginLoadOrder.POSTWORLD, "1.19",
        authors = listOf("goncalomb", "Team Monumenta"),
        depends = listOf("CommandAPI"),
        softDepends = listOf("MonumentaNetworkRelay"),
        apiJarVersion = "1.20.4-R0.1-SNAPSHOT"
    )

    versionAdapterApi("adapter_api", paper = "1.19.4") {
        dependencies {
            implementation(commons)
        }
    }
    versionAdapter("adapter_v1_18_R2", "1.18.2")
    versionAdapter("adapter_v1_19_R3", "1.19.4")
    versionAdapter("adapter_v1_20_R3", "1.20.4")
}

allprojects.forEach{
    it.tasks.withType<Javadoc> {
        (options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        (options as CoreJavadocOptions).addStringOption("-quiet")
    }
}
