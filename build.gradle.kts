plugins {
    id("org.quiltmc.loom") version "1.2.+"
    id("io.github.p03w.machete") version "2.+"
    id("org.cadixdev.licenser") version "0.6.+"
}

apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/quilt/publishing.gradle.kts")
apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/quilt/misc.gradle.kts")

val mod_version: String by project

group = "io.github.jamalam360"

version = mod_version

repositories {
    val mavenUrls =
            mapOf(
                    Pair("https://maven.terraformersmc.com/releases", listOf("com.terraformersmc", "dev.emi")),
                    Pair("https://api.modrinth.com/maven", listOf("maven.modrinth")),
                    Pair("https://maven.wispforest.io", listOf("io.wispforest")),
                    Pair("https://maven.jamalam.tech/releases", listOf("io.github.jamalam360")),
                    Pair("https://jitpack.io", listOf("com.github.llamalad7.mixinextras")),
                    Pair("https://maven2.bai.lol", listOf("lol.bai", "mcp.mobius.waila")),
            )

    for (mavenPair in mavenUrls) {
        maven {
            url = uri(mavenPair.key)
            content {
                for (group in mavenPair.value) {
                    includeGroup(group)
                }
            }
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.quilt.mappings) { classifier("intermediary-v2") })

    modImplementation(libs.bundles.quilt)
    modApi(libs.bundles.required)
    modImplementation(libs.bundles.optional)
    modRuntimeOnly(libs.bundles.runtime)
    include(libs.bundles.include)

    modCompileOnly(libs.waila.api)
    modCompileOnly(variantOf(libs.emi) {
        classifier("api")
    })

    annotationProcessor(libs.mixin.extras)
}

sourceSets {
    getByName("main") {
        resources.srcDir(rootProject.file("src/main/generated"))
    }
}

loom {
    runs {
        create("datagenClient") {
            inherit(getByName("client"))
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${rootProject.file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.modid=honk")

            runDir("build/datagen")
        }
    }
}

tasks {
    named("modrinth") {
        dependsOn("optimizeOutputsOfRemapJar")
    }
}
