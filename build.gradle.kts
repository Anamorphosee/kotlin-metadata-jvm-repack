import org.gradle.api.attributes.java.TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
    `maven-publish`
    signing
    id("com.gradleup.nmcp") version "1.2.0"
}

group = "dev.reformator.kotlin-metadata-jvm-repack"
version = "0.9.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:$version")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
    withJavadocJar()
    withSourcesJar()
}

afterEvaluate {
    configurations.forEach { conf ->
        if (conf.attributes.getAttribute(TARGET_JVM_VERSION_ATTRIBUTE) == 9) {
            conf.attributes.attribute(TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
        conf.outgoing.variants.forEach { variant ->
            if (variant.attributes.getAttribute(TARGET_JVM_VERSION_ATTRIBUTE) == 9) {
                variant.attributes.attribute(TARGET_JVM_VERSION_ATTRIBUTE, 8)
            }
        }
    }
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("kotlinx.metadata", "dev.reformator.kmetarepack")
    mergeServiceFiles()
    dependencies {
        include(dependency("org.jetbrains.kotlinx:kotlinx-metadata-jvm"))
    }
    excludes.remove("module-info.class")
    exclude("**/_dummy.class")
}

val mavenPublicationName = "maven"

publishing {
    publications {
        create<MavenPublication>(mavenPublicationName) {
            from(components["shadow"])
            artifact(tasks.named("sourcesJar"))
            artifact(tasks.named("javadocJar"))
            pom {
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    (dependenciesNode as groovy.util.Node).appendNode("dependency").apply {
                        appendNode("groupId", "org.jetbrains.kotlin")
                        appendNode("artifactId", "kotlin-stdlib")
                        appendNode("version", "1.9.21")
                        appendNode("scope", "runtime")
                    }
                }
                name.set("Kotlin metadata JVM library repack.")
                description.set("Kotlin metadata JVM library repack supporting JPMS.")
                url.set("https://github.com/Anamorphosee/kotlin-metadata-jvm-repack")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("Denis Berestinskii")
                        email.set("berestinsky@gmail.com")
                        url.set("https://github.com/Anamorphosee")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Anamorphosee/kotlin-metadata-jvm-repack.git")
                    developerConnection.set("scm:git:ssh://github.com:Anamorphosee/kotlin-metadata-jvm-repack.git")
                    url.set("http://github.com/Anamorphosee/kotlin-metadata-jvm-repack/tree/main")
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications[mavenPublicationName])
}

nmcp {
    publishAllPublicationsToCentralPortal {
        username = properties["sonatype.username"] as String?
        password = properties["sonatype.password"] as String?
        publishingType = "USER_MANAGED"
    }
}
