package io.github.jamalam360.honk.compatibility.waila;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.util.Identifier;

public class WailaCompatibility implements IWailaPlugin {

    public static final Identifier SHOW_GENES = HonkInit.idOf("show_genes");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addSyncedConfig(SHOW_GENES, true, false);
        registrar.addComponent(HonkOverride.INSTANCE, TooltipPosition.BODY, HonkEntity.class);
        registrar.addComponent(HonkOverride.INSTANCE, TooltipPosition.BODY, EggEntity.class);
    }
}
