import com.github.asyncmc.protocol.bedrock.BedrockProtocolModuleLoader;

module com.github.asyncmc.protocol.bedrock {
    requires com.github.asyncmc.protocol.raknet.api;

    requires com.github.asyncmc.module.api;
    provides com.github.asyncmc.module.api.ModuleLoader with BedrockProtocolModuleLoader;

    requires kotlin.stdlib;
    requires ktor.network;
    requires kotlinx.coroutines.core;
    requires ktor.utils.jvm;
    requires ktor.io.jvm;
    requires jctools.core;
}
