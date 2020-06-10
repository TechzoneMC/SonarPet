dependencies {
    // NOTE: We're compiling against paper now, too much hassle to compile Craftbukkit
    compileOnly("com.destroystokyo.paper:paper:1.13.1-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
}
