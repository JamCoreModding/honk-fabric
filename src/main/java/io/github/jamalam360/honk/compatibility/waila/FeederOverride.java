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
import io.github.jamalam360.honk.compatibility.WailaLike;
import io.wispforest.owo.nbt.NbtKey;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;

public class FeederOverride implements IBlockComponentProvider, IDataProvider<FeederBlockEntity> {
	public static final FeederOverride INSTANCE = new FeederOverride();
	private static final NbtKey<ItemStack> STACK_KEY = new NbtKey<>("Stack", NbtKey.Type.ITEM_STACK);

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		tooltip.addLine(WailaLike.getFeederTooltip(accessor.getData().raw().get(STACK_KEY)));
	}

	@Override
	public void appendData(IDataWriter data, IServerAccessor<FeederBlockEntity> accessor, IPluginConfig config) {
		data.raw().put(STACK_KEY, accessor.getTarget().getStack(0));
	}
}
