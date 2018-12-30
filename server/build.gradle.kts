plugins {
    kotlin("jvm")
    dokka()
    idea
    generating("r")
    generating("buildconfig")
}

group = RELEASE_GROUP
version = RELEASE_VERSION

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    getByName("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

ktlint()

dependencies {
    api(project(":core-jre"))

    implementation(ktor("server-netty"))
    implementation(ktor("gson"))

    implementation("ch.qos.logback:logback-classic:$VERSION_LOGBACK")
    implementation("org.mongodb:mongo-java-driver:$VERSION_MONGODB")

    testImplementation(junit())
    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(slf4j("log4j12"))
}

tasks {
    named<com.hendraanggrian.generating.r.RTask>("generateR") {
        resourcesDirectory = projectDir.resolve("res")
        properties {
            readResourceBundle = true
        }
    }

    named<com.hendraanggrian.generating.buildconfig.BuildConfigTask>("generateBuildConfig") {
        appName = "$RELEASE_NAME Server"
        debug = RELEASE_DEBUG
        website = RELEASE_WEBSITE
        field("DATABASE", RELEASE_ARTIFACT)
        field("DATABASE_USER", envUser())
        field("DATABASE_PASS", envPass())
    }

    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}