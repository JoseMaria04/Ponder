package net.createmod.catnip.impl.fabric;

import net.createmod.catnip.api.Catnip;
import net.createmod.catnip.api.event.ServerCommandRegistrationCallback;
import net.createmod.catnip.impl.command.argument.ConfigIdArgument;
import net.createmod.catnip.impl.command.argument.ConfigPathArgument;
import net.createmod.catnip.impl.command.argument.ModIdArgument;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

public final class CatnipFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		// custom argument types must be registered or the command tree can't be sent to clients
		ArgumentTypeRegistry.registerArgumentType(Catnip.id("config_id"), ConfigIdArgument.class, SingletonArgumentInfo.contextFree(ConfigIdArgument::configId));
		ArgumentTypeRegistry.registerArgumentType(Catnip.id("config_path"), ConfigPathArgument.class, SingletonArgumentInfo.contextFree(ConfigPathArgument::path));
		ArgumentTypeRegistry.registerArgumentType(Catnip.id("mod_id"), ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modId));

		Catnip.init();
		CommandRegistrationCallback.EVENT.register(ServerCommandRegistrationCallback.EVENT.invoker()::register);
	}
}
