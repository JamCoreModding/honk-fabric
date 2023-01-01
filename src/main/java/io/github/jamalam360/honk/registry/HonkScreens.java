package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeScreenHandler;
import io.github.jamalam360.honk.block.dna.DNAInjectorExtractorScreenHandler;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.screen.ScreenHandlerType;

@SuppressWarnings("Convert2MethodRef")
@ContentRegistry(HonkInit.MOD_ID)
public class HonkScreens {

    public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE = new ScreenHandlerType<>((syncId, inventory) -> new CentrifugeScreenHandler(syncId, inventory));
    public static final ScreenHandlerType<DNAInjectorExtractorScreenHandler> DNA_INJECTOR_EXTRACTOR = new ScreenHandlerType<>((syncId, inventory) -> new DNAInjectorExtractorScreenHandler(syncId, inventory));
}
