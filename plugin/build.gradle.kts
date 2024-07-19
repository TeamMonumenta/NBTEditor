import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.errorprone.CheckSeverity

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.goncalomb.bukkit.java-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1" // Generates plugin.yml
    id("net.ltgt.errorprone") version "2.0.2"
    id("net.ltgt.nullaway") version "1.3.0"
    id("com.playmonumenta.deployment") version "1.+"
}

dependencies {
    implementation(project(":adapter_api"))
    implementation(project(":adapter_v1_18_R2", "reobf"))
    implementation(project(":adapter_v1_19_R3", "reobf"))
    implementation("org.bstats:bstats-bukkit:1.5")
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    errorprone("com.google.errorprone:error_prone_core:2.10.0")
    errorprone("com.uber.nullaway:nullaway:0.9.5")
    implementation("org.apache.commons:commons-lang3:3.0")
}

group = "com.playmonumenta"
description = "NBTEditor"
version = rootProject.version

// Configure plugin.yml generation
bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "com.goncalomb.bukkit.nbteditor.NBTEditor"
    apiVersion = "1.13"
    name = "NBTEditor"
    authors = listOf("goncalomb", "Combustible")
    depend = listOf()
    softDepend = listOf()
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.shadowJar)
            artifact(tasks["sourcesJar"])
        }
    }
    repositories {
        maven {
            name = "MonumentaMaven"
            url = when (version.toString().endsWith("SNAPSHOT")) {
                true -> uri("https://maven.playmonumenta.com/snapshots")
                false -> uri("https://maven.playmonumenta.com/releases")
            }

            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xmaxwarns")
    options.compilerArgs.add("10000")
    options.compilerArgs.add("-Xlint:deprecation")

    options.errorprone {
		// TODO: Consider annotating packages with @Nullable and enabling this
        option("NullAway:AnnotatedPackages", "com.goncalomb.DISABLE")

        allErrorsAsWarnings.set(true)

        /*** Disabled checks ***/
        // These we almost certainly don't want
        check("CatchAndPrintStackTrace", CheckSeverity.OFF) // This is the primary way a lot of exceptions are handled
        check("FutureReturnValueIgnored", CheckSeverity.OFF) // This one is dumb and doesn't let you check return values with .whenComplete()
        check("ImmutableEnumChecker", CheckSeverity.OFF) // Would like to turn this on but we'd have to annotate a bunch of base classes
        check("LockNotBeforeTry", CheckSeverity.OFF) // Very few locks in our code, those that we have are simple and refactoring like this would be ugly
        check("StaticAssignmentInConstructor", CheckSeverity.OFF) // We have tons of these on purpose
        check("StringSplitter", CheckSeverity.OFF) // We have a lot of string splits too which are fine for this use
        check("MutablePublicArray", CheckSeverity.OFF) // These are bad practice but annoying to refactor and low risk of actual bugs
        check("InlineMeSuggester", CheckSeverity.OFF) // This seems way overkill
    }
}

ssh.easySetup(tasks.named<ShadowJar>("shadowJar").get(), "NBTEditor")
