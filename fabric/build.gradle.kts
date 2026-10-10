plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.configure.platform)
}

loom {
    mods.register("ponder") {
        sourceSet(sourceSets.main.get())
        sourceSet(sourceSets.client.get())
    }

    runs {
        named("server") {
            runDirectory = file("run/server")
        }

        configureEach {
            generateRunConfig = true
            jvmArguments.add("-Dmixin.debug.export=true")
            jvmArguments.add("-XX:+IgnoreUnrecognizedVMOptions")
            jvmArguments.add("-XX:+AllowEnhancedClassRedefinition")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    api(libs.bundles.fabric)
    api(project(":catnip-fabric"))
    clientCompileOnly(project(":catnip-fabric", configuration = "client"))
    // 26.3 port: Catnip's SuperByteBuffer extends Flywheel's TransformStack
    compileOnly("dev.engine-room.flywheel:flywheel-fabric:1.0.6-mc26.3")
}

fabricApi {
    configureTests {
        createSourceSet = true
        modId = "ponder_test"
        enableGameTests = false
        enableClientGameTests = true
        eula = true
    }
}

dependencies {
    "gametestImplementation"(project(":catnip-fabric"))
}
