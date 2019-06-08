// Make sure JAVA_HOME is set
val javaHome = System.getProperty("java.home")

val kotlinVersion by extra("1.3.21")
val moduleName by extra("jpkg")
val mainClass by extra("jpkg.AppKt")

plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.21")
    application
}

repositories {
    jcenter()
}

dependencies {
    // Have to list these 3 modules or jlink will complain
    // about "Error: automatic module cannot be used with jlink"
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion:modular")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion:modular")
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion:modular")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    "compileJava"(JavaCompile::class) {
        inputs.property("moduleName", moduleName)
        doFirst {
            options.compilerArgs = listOf(
                    "--module-path", classpath.asPath,
                    "--patch-module", "$moduleName=${sourceSets["main"].output.asPath}"
            )
            classpath = files()
        }
    }

    val jar by getting(Jar::class)

    val jlink by registering(Exec::class) {
        val outputDir by extra("$buildDir/jlink")
        inputs.files(configurations.runtimeClasspath)
        inputs.files(jar.archiveFile)
        outputs.dir(outputDir)
        dependsOn(jar)
        doFirst {
            val runtimeClasspath = configurations.runtimeClasspath.get()
            println("classpaths: " + runtimeClasspath.toList())
            delete(outputDir)
            commandLine("jlink",
                    "--module-path",
                    listOf("$javaHome/jmods/", runtimeClasspath.asPath, jar.archiveFile.get()).joinToString(File.pathSeparator),
                    "--add-modules", moduleName,
                    "--launcher", "${rootProject.name}=$moduleName/$mainClass",
                    "--output", outputDir
            )
        }
    }
}

application {
    // Define the main class for the application.
    mainClassName = mainClass
}
