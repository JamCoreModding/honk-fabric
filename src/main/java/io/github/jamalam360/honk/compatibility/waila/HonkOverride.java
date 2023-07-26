/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
		DnaData data = null;

		if (accessor.getEntity() instanceof HonkEntity honk) {
			data = honk.createDnaData();
		} else if (accessor.getEntity() instanceof EggEntity egg) {
			data = egg.createDnaData();
		}

		if (data != null && config.getBoolean(WailaCompatibility.SHOW_GENES)) {
			if (accessor.getEntity() instanceof HonkEntity honk) {
				tooltip.addLine(Text.translatable("text.honk.waila.food", honk.getFoodLevel()));
			} else if (accessor.getEntity() instanceof EggEntity egg) {
				tooltip.addLine(Text.translatable("text.honk.waila.age", egg.getAge() / 20));
				tooltip.addLine(Text.translatable("text.honk.waila.warm_" + (egg.isWarm() ? "yes" : "no")));
			}

			if (!config.getBoolean(WailaCompatibility.ONLY_SHOW_GENES_ON_SNEAK) || accessor.getPlayer().isSneaking()) {
				tooltip.addLine(Text.translatable("text.honk.waila.productivity", data.productivity()));
				tooltip.addLine(Text.translatable("text.honk.waila.reproductivity", data.reproductivity()));
				tooltip.addLine(Text.translatable("text.honk.waila.growth", data.growth()));
				tooltip.addLine(Text.translatable("text.honk.waila.instability", data.instability()));
			}
		}
	}
}
