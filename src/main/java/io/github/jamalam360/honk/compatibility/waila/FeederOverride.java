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
