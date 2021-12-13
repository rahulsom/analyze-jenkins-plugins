plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.31"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("com.squareup.retrofit2:retrofit:2.5.0")
  implementation("com.squareup.retrofit2:converter-scalars:2.5.0")
  implementation("com.squareup.retrofit2:converter-gson:2.5.0")
  
  implementation("com.squareup.okhttp3:okhttp:4.9.3")
  implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

  implementation("net.sf.supercsv:super-csv:2.4.0")

  implementation("commons-codec:commons-codec:1.15")
  implementation("commons-io:commons-io:2.11.0")
  implementation("org.fusesource.jansi:jansi:2.4.0")

  implementation("com.google.inject:guice:5.0.1")

  implementation("com.typesafe:config:1.4.1")

  implementation("org.slf4j:slf4j-api:1.7.32")
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.15.0")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  implementation(kotlin("reflect"))
}

application {
  mainClass.set("com.github.rahulsom.ajp.AppKt")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xopt-in=kotlin.Experimental")
  }
}
