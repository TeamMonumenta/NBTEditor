import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.core.Service
import org.hidetake.groovy.ssh.session.SessionHandler
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.errorprone.CheckSeverity

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.goncalomb.bukkit.java-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1" // Generates plugin.yml
    id("org.hidetake.ssh") version "2.10.1"
    id("net.ltgt.errorprone") version "2.0.2"
    id("net.ltgt.nullaway") version "1.3.0"
}

dependencies {
    implementation(project(":adapter_api"))
    implementation(project(":adapter_v1_16_R3"))
    implementation(project(":adapter_v1_18_R2", "reobf"))
    implementation("org.bstats:bstats-bukkit:1.5")
    compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
    errorprone("com.google.errorprone:error_prone_core:2.10.0")
    errorprone("com.uber.nullaway:nullaway:0.9.5")
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

publishing {
    publications.create<MavenPublication>("maven") {
        project.shadow.component(this)
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/TeamMonumenta/NBTEditor")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
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

val basicssh = remotes.create("basicssh") {
    host = "admin-eu.playmonumenta.com"
    port = 8822
    user = "epic"
    agent = System.getenv("IDENTITY_FILE") == null
    identity = if (System.getenv("IDENTITY_FILE") == null) null else file(System.getenv("IDENTITY_FILE"))
    knownHosts = allowAnyHosts
}

val adminssh = remotes.create("adminssh") {
    host = "admin-eu.playmonumenta.com"
    port = 9922
    user = "epic"
    agent = System.getenv("IDENTITY_FILE") == null
    identity = if (System.getenv("IDENTITY_FILE") == null) null else file(System.getenv("IDENTITY_FILE"))
    knownHosts = allowAnyHosts
}

// Relocation / shading
tasks {
    shadowJar {
       relocate("org.bstats", "com.goncalomb.bukkit.bstats")
    }
}

tasks.create("dev1-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(basicssh) {
                execute("cd /home/epic/dev1_shard_plugins && rm -f nbteditor*.jar")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/dev1_shard_plugins")
            }
        }
    }
}

tasks.create("dev2-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(basicssh) {
                execute("cd /home/epic/dev2_shard_plugins && rm -f nbteditor*.jar")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/dev2_shard_plugins")
            }
        }
    }
}

tasks.create("dev3-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(basicssh) {
                execute("cd /home/epic/dev3_shard_plugins && rm -f nbteditor*.jar")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/dev3_shard_plugins")
            }
        }
    }
}

tasks.create("dev4-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(basicssh) {
                execute("cd /home/epic/dev4_shard_plugins && rm -f nbteditor*.jar")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/dev4_shard_plugins")
            }
        }
    }
}

tasks.create("futurama-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(basicssh) {
                execute("cd /home/epic/futurama_shard_plugins && rm -f nbteditor*.jar")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/futurama_shard_plugins")
            }
        }
    }
}

tasks.create("mobs-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(basicssh) {
                execute("cd /home/epic/mob_shard_plugins && rm -f nbteditor*.jar")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/mob_shard_plugins")
            }
        }
    }
}

tasks.create("stage-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(basicssh) {
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/stage/m12/server_config/plugins")
                execute("cd /home/epic/stage/m12/server_config/plugins && rm -f nbteditor.jar && ln -s " + shadowJar.archiveFileName.get() + " nbteditor.jar")
            }
        }
    }
}

tasks.create("build-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(adminssh) {
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/project_epic/server_config/plugins")
                execute("cd /home/epic/project_epic/server_config/plugins && rm -f nbteditor.jar && ln -s " + shadowJar.archiveFileName.get() + " nbteditor.jar")
                execute("cd /home/epic/project_epic/mobs/plugins && rm -f nbteditor.jar && ln -s ../../server_config/plugins/nbteditor.jar")
            }
        }
    }
}

tasks.create("play-deploy") {
    val shadowJar by tasks.named<ShadowJar>("shadowJar")
    dependsOn(shadowJar)
    doLast {
        ssh.runSessions {
            session(adminssh) {
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/play/m8/server_config/plugins")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/play/m11/server_config/plugins")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/play/m13/server_config/plugins")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/play/m14/server_config/plugins")
                put(shadowJar.archiveFile.get().getAsFile(), "/home/epic/play/m15/server_config/plugins")
                execute("cd /home/epic/play/m8/server_config/plugins && rm -f nbteditor.jar && ln -s " + shadowJar.archiveFileName.get() + " nbteditor.jar")
                execute("cd /home/epic/play/m11/server_config/plugins && rm -f nbteditor.jar && ln -s " + shadowJar.archiveFileName.get() + " nbteditor.jar")
                execute("cd /home/epic/play/m13/server_config/plugins && rm -f nbteditor.jar && ln -s " + shadowJar.archiveFileName.get() + " nbteditor.jar")
                execute("cd /home/epic/play/m14/server_config/plugins && rm -f nbteditor.jar && ln -s " + shadowJar.archiveFileName.get() + " nbteditor.jar")
                execute("cd /home/epic/play/m15/server_config/plugins && rm -f nbteditor.jar && ln -s " + shadowJar.archiveFileName.get() + " nbteditor.jar")
            }
        }
    }
}

fun Service.runSessions(action: RunHandler.() -> Unit) =
    run(delegateClosureOf(action))

fun RunHandler.session(vararg remotes: Remote, action: SessionHandler.() -> Unit) =
    session(*remotes, delegateClosureOf(action))

fun SessionHandler.put(from: Any, into: Any) =
    put(hashMapOf("from" to from, "into" to into))
