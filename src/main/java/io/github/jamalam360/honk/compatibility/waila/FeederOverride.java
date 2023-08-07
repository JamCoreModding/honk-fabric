package io.github.jamalam360.honk.compatibility.waila;

import io.github.jamalam360.honk.block.feeder.FeederBlockEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.text.Text;

public class FeederOverride implements IBlockComponentProvider, IDataProvider<FeederBlockEntity> {
	public static final FeederOverride INSTANCE = new FeederOverride();

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		boolean empty = accessor.getData().raw().getBoolean("Empty");
		int count = accessor.getData().raw().getInt("Count");
		int maxCount = accessor.getData().raw().getInt("MaxCount");
		String translation = accessor.getData().raw().getString("Translation");

		if (empty) {
			tooltip.addLine(Text.translatable("text.honk.waila.feeder_empty"));
		} else {
			tooltip.addLine(Text.translatable(translation));
			tooltip.addLine(Text.literal(count + "/" + maxCount));
		}
	}

	@Override
	public void appendData(IDataWriter data, IServerAccessor<FeederBlockEntity> accessor, IPluginConfig config) {
		data.raw().putBoolean("Empty", accessor.getTarget().getStack(0).isEmpty());
		data.raw().putInt("Count", accessor.getTarget().getStack(0).getCount());
		data.raw().putInt("MaxCount", accessor.getTarget().getStack(0).getMaxCount());
		data.raw().putString("Translation", accessor.getTarget().getStack(0).getTranslationKey());
	}
}
