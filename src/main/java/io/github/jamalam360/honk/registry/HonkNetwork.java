package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.jamlib.network.JamLibS2CNetworkChannel;

public class HonkNetwork {
	public static final JamLibS2CNetworkChannel S2C_FUEL_BURNING_UPDATE_BURN_TIME = new JamLibS2CNetworkChannel(HonkInit.idOf("fuel_burning_update_burn_time"));
}
