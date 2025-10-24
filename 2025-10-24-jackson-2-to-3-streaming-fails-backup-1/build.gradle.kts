plugins { kotlin("jvm") version "2.2.20"; jacoco }

layout.buildDirectory = file("$projectDir/.build")

repositories { mavenCentral() }
kotlin { jvmToolchain(21) }
dependencies {
	implementation(kotlin("reflect"))

	implementation("commons-io:commons-io:2.20.0")

	implementation("tools.jackson.core:jackson-core:3.0.1")
	implementation("tools.jackson.core:jackson-databind:3.0.1")
	implementation("tools.jackson.module:jackson-module-kotlin:3.0.1")

	implementation("com.fasterxml.jackson.core:jackson-core:2.20.0")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")

	testImplementation("org.assertj:assertj-core:3.27.6")
	testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0")
}

tasks {
	test { useJUnitPlatform() }
	test { finalizedBy(jacocoTestReport) }
	jacocoTestReport {
		dependsOn(test)
		reports.xml.required = true
		reports.html.required = false
	}
	withType<JacocoReport> { classDirectories.setFrom(sourceSets.main.get().output.asFileTree) }
}
