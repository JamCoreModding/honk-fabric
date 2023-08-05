package io.github.jamalam360.honk;

import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.registry.HonkS2CNetwork;
import net.minecraft.network.PacketByteBuf;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.server.DedicatedServerModInitializer;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;

public class HonkServerInit implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer(ModContainer mod) {
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			HonkInit.LOGGER.info("Sending " + HonkType.ENTRIES.size() + " honk types to newly connected client");
			PacketByteBuf buf = PacketByteBufs.create();
			HonkS2CNetwork.writeHonkTypePacket(buf);
			sender.sendPacket(HonkInit.idOf("honk_types"), buf);
		}));
	}
}
