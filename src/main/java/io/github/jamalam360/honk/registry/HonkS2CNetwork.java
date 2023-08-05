package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.jamlib.network.JamLibS2CNetworkChannel;
import net.minecraft.network.PacketByteBuf;

public class HonkS2CNetwork {
	public static final JamLibS2CNetworkChannel HONK_TYPES = new JamLibS2CNetworkChannel(HonkInit.idOf("honk_types"));

	public static void writeHonkTypePacket(PacketByteBuf buf) {
		buf.writeInt(HonkType.ENTRIES.size());
		HonkType.ENTRIES.values().forEach((t) -> t.toPacket(buf));
	}
}
