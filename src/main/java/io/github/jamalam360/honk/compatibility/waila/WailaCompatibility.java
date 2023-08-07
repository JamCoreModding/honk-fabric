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

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.feeder.FeederBlock;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.util.Identifier;

public class WailaCompatibility implements IWailaPlugin {

	public static final Identifier SHOW_GENES = HonkInit.idOf("show_genes");
	public static final Identifier ONLY_SHOW_GENES_ON_SNEAK = HonkInit.idOf("only_show_genes_on_sneak");

	@Override
	public void register(IRegistrar registrar) {
		HonkInit.LOGGER.info("Initializing Honk compatibility module for WAILA and derivatives");

		registrar.addSyncedConfig(SHOW_GENES, true, false);
		registrar.addConfig(ONLY_SHOW_GENES_ON_SNEAK, false);
		registrar.addComponent(HonkOverride.INSTANCE, TooltipPosition.BODY, HonkEntity.class);
		registrar.addComponent(HonkOverride.INSTANCE, TooltipPosition.BODY, EggEntity.class);
		registrar.addComponent(FeederOverride.INSTANCE, TooltipPosition.BODY, FeederBlock.class);
		registrar.addBlockData(FeederOverride.INSTANCE, FeederBlock.class);
	}
}
