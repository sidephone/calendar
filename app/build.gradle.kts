plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.androidx.room3)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
}

android {
	namespace = "com.sidephone.calendar"
	compileSdk {
		version = release(37)
	}

	defaultConfig {
		applicationId = "com.sidephone.calendar"
		minSdk = 31
		targetSdk = 37
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			optimization {
				enable = true
			}
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures {
		compose = true
	}

	tasks
		.matching { it.name == "assembleDebug" || it.name == "assembleRelease" }
		.configureEach { dependsOn("testDebugUnitTest") }
}

room3 {
	schemaDirectory("$projectDir/schemas")
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.room3.runtime)
	implementation(libs.androidx.sqlite.bundled)
	ksp(libs.androidx.room3.compiler)
	testImplementation(kotlin("test"))
	testImplementation(kotlin("test-junit"))
	debugImplementation(libs.androidx.compose.ui.test.manifest)
	debugImplementation(libs.androidx.compose.ui.tooling)
}
