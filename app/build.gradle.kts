import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.21"
}

group = "com.github.livingwithhippos"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

val ktlint: Configuration by configurations.creating

dependencies {

    val kotlinVersion = "1.4.21"
    val coroutinesVersion = "1.4.2"
    val telegramVersion = "6.0.2"
    val moshiVersion = "1.11.0"
    val retrofitVersion = "2.9.0"
    val okhttpVersion = "4.9.0"
    val koinVersion = "2.2.2"
    val ktLintVersion = "0.40.0"

    // kotlin stdlib
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // Koin for Kotlin
    implementation ("org.koin:koin-core:$koinVersion")
    // Koin extended & experimental features
    implementation ("org.koin:koin-core-ext:$koinVersion")

    // telegram bot
    implementation ("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:$telegramVersion")

    // moshi
    implementation ("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    kapt ("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation ("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    //okhttp
    implementation ("com.squareup.okhttp3:okhttp:$okhttpVersion")
    //okhttp logging
    implementation ("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    ktlint("com.pinterest:ktlint:$ktLintVersion")
}

tasks {

    // creates a fat jar (with dependencies) for Docker, using ./gradlew Jar
    withType<Jar> {

        manifest.attributes["Main-Class"] = "com.github.livingwithhippos.unchained_bot.MainKt"
        // remove the version from the Jar to make it easier to launch in the Dockerfile
        setProperty("archiveVersion","")

        configurations["compileClasspath"].forEach { file: File ->
            from(zipTree(file.absoluteFile))
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

// ktlint stuff
val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}