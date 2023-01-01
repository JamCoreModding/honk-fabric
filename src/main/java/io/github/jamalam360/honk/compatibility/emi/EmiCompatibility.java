package io.github.jamalam360.honk.compatibility.emi;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class EmiCompatibility implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        HonkInit.LOGGER.info("Initializing EMI compatibility module...");
    }
}
