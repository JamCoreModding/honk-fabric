package io.github.jamalam360.honk.compatibility.waila;

import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.text.Text;

public class HonkOverride implements IEntityComponentProvider {

    public static final HonkOverride INSTANCE = new HonkOverride();

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        //TODO: test for honk and egg, and test config option
        DnaData data = null;

        if (accessor.getEntity() instanceof HonkEntity honk) {
            data = honk.createDnaData();
        } else if (accessor.getEntity() instanceof EggEntity egg) {
            data = egg.createDnaData();
        }

        if (data != null && config.getBoolean(WailaCompatibility.SHOW_GENES)) {
            tooltip.addLine(Text.translatable("text.honk.info_productivity", data.productivity()));
            tooltip.addLine(Text.translatable("text.honk.info_reproductivity", data.reproductivity()));
            tooltip.addLine(Text.translatable("text.honk.info_growth", data.growth()));
            tooltip.addLine(Text.translatable("text.honk.info_instability", data.instability()));
        }
    }
}
