/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id("application")
}

val log4jVersion = providers.environmentVariable("LOG4J_VERSION").getOrElse("2.+")

repositories {
    maven(providers.environmentVariable("LOG4J_REPOSITORY_URL").getOrElse("https://repo.maven.apache.org/maven2"))
}

application {
    mainModule = "org.example.log4j.metadata"
    mainClass = "org.example.App" // see: src/main/java/org/example/App.java
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Werror") // treat all warnings as errors
    options.compilerArgs.add("-Xlint:all") // includes 'classfile' check
}

// Check the classpaths
val expectedRuntimeClasspath = listOf(
    "log4j-api"
)
val expectedCompileClasspath = listOf(
    "biz.aQute.bnd.annotation",
    "error_prone_annotations",
    "jspecify",
    "jsr305",
    "log4j-api",
    "org.osgi.annotation.bundle",
    "org.osgi.annotation.versioning",
    "org.osgi.resource",
    "org.osgi.service.serviceloader",
    "spotbugs-annotations"
)

tasks.register("assertRuntimeClasspath") {
    inputs.files(configurations.runtimeClasspath)
    doLast {
        val actual = inputs.files.map { it.name.replace(Regex("-[0-9].*"), "") }
        assert(actual.sorted() == expectedRuntimeClasspath) {
            "Unexpected runtime classpath: $actual"
        }
    }
}
tasks.register("assertCompileClasspath") {
    inputs.files(configurations.compileClasspath)
    doLast {
        val actual = inputs.files.map { it.name.replace(Regex("-[0-9].*"), "") }
        assert(actual.sorted() == expectedCompileClasspath) {
            "Unexpected compile classpath: $actual"
        }
    }
}

tasks.check {
    dependsOn(tasks.named("assertRuntimeClasspath"))
    dependsOn(tasks.named("assertCompileClasspath"))
}
