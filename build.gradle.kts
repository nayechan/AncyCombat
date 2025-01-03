import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.nayechan.combat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.oraxen.com/releases")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("com.j256.ormlite:ormlite-core:6.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    
    compileOnly("io.th0rgal:oraxen:1.182.0") 
    {
        exclude("me.gabytm.util", "actions-spigot")
        exclude("org.jetbrains", "annotations")
        exclude("com.ticxo", "PlayerAnimator")
        exclude("com.github.stefvanschie.inventoryframework", "IF")
        exclude("io.th0rgal", "protectionlib")
        exclude("dev.triumphteam", "triumph-gui")
        exclude("org.bstats", "bstats-bukkit")
        exclude("com.jeff-media", "custom-block-data")
        exclude("com.jeff-media", "persistent-data-serializer")
        exclude("com.jeff-media", "MorePersistentDataTypes")
        exclude("gs.mclo", "java")
    }

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}
tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    relocate("com.j256.ormlite", "com.nayechan.combat.shaded.ormlite")
    
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
