rootProject.name = "otusJava"

include("l19hw09-jdbc")
include("l21hw10-jpql")
include("l22hw11-cache")
include("l24hw12-webServer")
include("l25hw13-di")
include("l28hw14-springDataJdbc")

pluginManagement {
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings

    plugins {
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow
    }
}


