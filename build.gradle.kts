import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}
plugins {
    id("java")
    id("maven")
    kotlin("jvm").version("1.3.72")
    id("com.github.johnrengelman.shadow")
            .version("5.2.0")
            .apply(false)
}
allprojects {
    apply {
        plugin("maven")
    }

    group = "net.techcable.sonarpet"
    version = "1.2.0-alpha1-SNAPSHOT"
}
var versionSignature: String by rootProject.extra
versionSignature = rawComputeVersionSignature()

subprojects {
    apply {
        plugin("java")
        plugin("kotlin")
    }
    configurations {
        create("shade") {
            description = "Shade into the output jar"
            isTransitive = true
        }
        "compile" {
            extendsFrom(configurations["shade"])
        }
    }
    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
    tasks.withType<Test> {
        maxParallelForks = 4
        useJUnit {
            this as JUnitOptions
            if (!project.hasProperty("runSlowTests") && System.getProperty("user.name") != "jenkins") {
                excludeCategories("net.techcable.sonarpet.test.SlowTest")
            }
            systemProperty("sonarpet.versionSignature", versionSignature)
        }
    }

    repositories {
        /*
         * NOTE: I have a bunch of local jars to keep
         * all this legacy code rolling. For now my laptop
         * is the only place you can compile SonarPet ^_^
         */
        mavenLocal()
        mavenCentral()
        maven {
            name = "techcable-repo"
            setUrl("https://repo.techcable.net/content/groups/public/")
        }
        maven {
            name = "paper-repo"
            setUrl("https://papermc.io/repo/repository/maven-public/")
        }
        maven { setUrl("http://repo.dmulloy2.net/content/groups/public/") }
        maven {
            name = "enginehub-repo"
            setUrl("https://maven.enginehub.org/repo/")
        }
        // TODO: What are all these things for!?!
        maven { setUrl("http://repo.md-5.net/content/groups/public/") }
        maven { setUrl("http://ci.hawkfalcon.com/plugin/repository/everything/") }
        maven { setUrl("http://maven.sk89q.com/repo/") }
        maven { setUrl("http://repo.kitteh.org/content/groups/public") }
        maven { setUrl("http://nexus.theyeticave.net/content/repositories/pub_releases") }
        maven { setUrl("http://maven.elmakers.com/repository/") }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testCompile("junit:junit:4.12")
        testCompile("com.googlecode.junit-toolbox:junit-toolbox:2.3")
        compile("net.techcable:pineapple:0.1.0-beta6")
        // TODO: Get rid of this
        // TODO: Drop transitive dependency on "Minecraft-Reflection"
        compile("com.dsh105:Commodus:1.0.6") {
            exclude(module = "Minecraft-Reflection")
        }
        // TODO: Get rid of this
        compile("com.dsh105:PowerMessage:1.0.1-SNAPSHOT") {
            exclude(module = "Commodus")
        }
        compile("org.slf4j:slf4j-jdk14:1.7.5")
        compile("com.zaxxer:HikariCP:2.4.5")
        /*
         * NOTE: Paper already includes ASM.
         *
         * Since we depend on them, will just assume its already present.
         * Shading in this dependency is unessicarry
         *
         * The bootstrap loader also depends on ASM to automatically relocate dependencies too,
         * since we need to keep our own seperate version of guava and other things.
         */
        compileOnly("org.ow2.asm:asm:8.0.1")
        compileOnly("org.ow2.asm:asm-commons:5.2")
        /*
         * This is not included in paper. It's required by the bootstrap loader,
         * so we have to shade it in directly.
         *
         * Not sure if it will causes issues with the not-shaded asm.
         */
        // TODO: Does this create issues since we don't shade the other parts of ASM?
        "shade"("org.ow2.asm:asm-util:5.2")
        // Provided dependencies
        /*
         * We only support recent minecraft.
         * Therefore we can compile against actually modern
         * guava versions.
         */
        compileOnly("com.google.guava:guava:21.0")
        // We compile against Paper API - not Bukkit or Spigot API
        compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
        compileOnly("org.projectlombok:lombok:1.16.12")
        // TODO: Is VanishNoPacket still the standard?
        // Do we need to integrate with someone elese?
        compileOnly("org.kitteh:VanishNoPacket:3.18.7") {
            exclude(module = "Vault")
        }
        compileOnly("com.sk89q:worldedit:6.0.0-SNAPSHOT")
        compileOnly("com.sk89q:worldguard:6.0.0-SNAPSHOT")
        // TODO: Update to modern ProtocolLib
        compileOnly("com.comphenix.protocol:ProtocolLib:4.5.0")
    }

    // All test modules depend on the API's test module
    if (name != "api") {
        val apiTests = project(":api").convention.getPlugin(JavaPluginConvention::class).sourceSets["test"].output
        dependencies {
            testCompile(apiTests)
        }
    }
}

fun rawComputeVersionSignature(): String {
    val version = project.version.toString()
    if (!version.endsWith("-SNAPSHOT")) {
        // If it's not a snapshot version, there's not much to do
        return version + "-release"
    }
    val versionBase = version.replace("-SNAPSHOT", "")
    // Determine the current git commit
    // Determine if there are uncommitted changes
    var prog = ProcessBuilder("git", "status", "--porcelain").redirectOutput(ProcessBuilder.Redirect.PIPE).start()
    val isClean = prog.inputStream.reader().use { it.readText().isBlank() }
    check(prog.waitFor() == 0) { "Failed to execute git status!" }
    /*
     * NOTE: Prefer the short option over manual slicing since it handles uniqueness.
     * If we ever run into hash collisions in the first few chars, it'll still work.
     */
    prog     = ProcessBuilder("git", "rev-parse", "--short", "HEAD").redirectOutput(ProcessBuilder.Redirect.PIPE).start()
    val currentCommit = prog.inputStream.reader().use { it.readText().trim() }
    check(prog.waitFor() == 0) { "Failed to execute git rev-parse!" }
    val statusText = if (isClean) "dev" else "dirty"
    return "$versionBase-$statusText-$currentCommit"
}
