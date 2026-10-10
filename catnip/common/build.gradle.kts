plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.configure.platform)
}

dependencies {
    minecraft(libs.minecraft)
    compileOnly(libs.bundles.mixin)
    // 26.3 port: SuperByteBuffer extends Flywheel's TransformStack again (as in Catnip 1.21.1)
    compileOnly(libs.flywheel.fabric)
    compileOnly(libs.flywheel.fabric) {
        capabilities {
            requireFeature("api")
        }
    }
}

loom {
    accessWidenerPath = file("catnip_common_source.accesswidener")
}
