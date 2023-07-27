package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.jamlib.network.JamLibC2SNetworkChannel;

public class HonkC2SNetwork {
	public static final JamLibC2SNetworkChannel HONK = new JamLibC2SNetworkChannel(HonkInit.idOf("honk"));

}
