import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("com.google.protobuf") version "0.8.18"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

sourceSets {
    main {
        proto {
            srcDir("src/main/protobuf")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.protobuf:protobuf-kotlin:3.19.4")
    api("io.grpc:grpc-protobuf:1.44.0")
    api("com.google.protobuf:protobuf-java-util:3.19.4")
    api("com.google.protobuf:protobuf-kotlin:3.19.4")
    api("io.grpc:grpc-kotlin-stub:1.2.1")
    api("io.grpc:grpc-stub:1.44.0")
    runtimeOnly("io.grpc:grpc-netty:1.44.0")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.slf4j:slf4j-api:1.7.9")
    implementation("org.slf4j:slf4j-simple:1.7.9")
    implementation("ch.qos.logback:logback-classic:1.0.13")
    implementation("ch.qos.logback:logback-core:1.0.13")
    implementation("org.slf4j:slf4j-log4j12:1.7.36")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation("org.hibernate:hibernate-entitymanager:5.3.7.Final")
    implementation("org.hibernate:hibernate-c3p0:5.3.7.Final")
    implementation("com.zaxxer:HikariCP:2.4.6")

//    implementation ("org.springframework.boot:spring-boot-starter-actuator")
//    implementation ("io.micrometer:micrometer-registry-prometheus")


//    implementation("org.springframework.boot:spring-boot-starter-web:2.7.2")
//    implementation("org.springframework.boot:spring-boot-starter-actuator:2.7.2")
//    implementation("io.micrometer:micrometer-registry-prometheus:1.9.2")

//    implementation("me.dinowernli:java-grpc-prometheus:0.3.0")
//    implementation("io.micrometer:micrometer-registry-prometheus:1.9.2")
//    implementation("org.springframework.boot:spring-boot-starter-actuator:2.0.5.RELEASE")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.44.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.2.1:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}