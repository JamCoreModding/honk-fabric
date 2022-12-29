package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeScreenHandler;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.screen.ScreenHandlerType;

@ContentRegistry(HonkInit.MOD_ID)
public class HonkScreens {

    @SuppressWarnings("Convert2MethodRef")
    public static final ScreenHandlerType<io.github.jamalam360.honk.block.centrifuge.CentrifugeScreenHandler> CENTRIFUGE = new ScreenHandlerType<>((syncId, inventory) -> new CentrifugeScreenHandler(syncId, inventory));
}
