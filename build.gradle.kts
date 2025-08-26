plugins {
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName.set(project.property("archives_name") as String)
    version = project.property("mod_version") as String
    group = project.property("maven_group") as String
}

repositories {
    maven("https://maven.meteordev.org/releases") { name = "Meteor Releases" }
    maven("https://maven.meteordev.org/snapshots") { name = "Meteor Snapshots" }
}

dependencies {
    minecraft(libs.minecraft.mojang)
    mappings(libs.yarn.mappings)
    modImplementation(libs.fabric.loader)
    modImplementation("meteordevelopment:meteor-client:${libs.versions.minecraft.get()}-SNAPSHOT")
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to project.version,
            "mc_version" to libs.versions.minecraft.get(),
            "mod_id" to project.property("mod_id")
        )
        inputs.properties(properties)
        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    jar {
        val finalArchivesName = base.archivesName.get()
        from("LICENSE") {
            rename { "${it}_$finalArchivesName" }
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}
