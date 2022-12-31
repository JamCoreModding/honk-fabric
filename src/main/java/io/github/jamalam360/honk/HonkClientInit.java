package io.github.jamalam360.honk;

import io.github.jamalam360.honk.block.centrifuge.CentrifugeScreen;
import io.github.jamalam360.honk.entity.egg.EggEntityModel;
import io.github.jamalam360.honk.entity.egg.EggEntityRenderer;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntityModel;
import io.github.jamalam360.honk.entity.honk.HonkEntityRenderer;
import io.github.jamalam360.honk.registry.HonkEntities;
import io.github.jamalam360.honk.registry.HonkScreens;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.Box;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientWorldTickEvents;

public class HonkClientInit implements ClientModInitializer {

    public static final EntityModelLayer EGG_LAYER = new EntityModelLayer(HonkInit.id("egg"), "main");
    public static final EntityModelLayer HONK_LAYER = new EntityModelLayer(HonkInit.id("honk"), "main");

    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(HonkEntities.EGG, EggEntityRenderer::new);
        EntityRendererRegistry.register(HonkEntities.HONK, HonkEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(EGG_LAYER, EggEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(HONK_LAYER, HonkEntityModel::getTexturedModelData);

        HandledScreens.register(HonkScreens.CENTRIFUGE, CentrifugeScreen::new);
    }
}
