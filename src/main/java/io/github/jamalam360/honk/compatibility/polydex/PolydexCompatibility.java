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

package io.github.jamalam360.honk.compatibility.polydex;

import eu.pb4.polydex.api.v1.hover.HoverDisplayBuilder;
import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.feeder.FeederBlockEntity;
import io.github.jamalam360.honk.compatibility.WailaLike;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.registry.HonkEntities;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@SuppressWarnings("deprecation")
public class PolydexCompatibility implements ModInitializer {
	private static final HoverDisplayBuilder.ComponentType FOOD_LEVEL = new HoverDisplayBuilder.ComponentType(
			HonkInit.idOf("food_level"),
			HoverDisplayBuilder.ComponentType.Visibility.SNEAKING,
			false,
			3
	);
	private static final HoverDisplayBuilder.ComponentType BREEDING_AGE = new HoverDisplayBuilder.ComponentType(
			HonkInit.idOf("breeding_age"),
			HoverDisplayBuilder.ComponentType.Visibility.SNEAKING,
			false,
			4
	);
	private static final HoverDisplayBuilder.ComponentType EGG_WARMTH = new HoverDisplayBuilder.ComponentType(
			HonkInit.idOf("egg_warmth"),
			HoverDisplayBuilder.ComponentType.Visibility.ALWAYS,
			false,
			4
	);
	private static final HoverDisplayBuilder.ComponentType EGG_AGE = new HoverDisplayBuilder.ComponentType(
			HonkInit.idOf("egg_age"),
			HoverDisplayBuilder.ComponentType.Visibility.SNEAKING,
			false,
			5
	);
	private static final HoverDisplayBuilder.ComponentType FEEDER_LEVEL = new HoverDisplayBuilder.ComponentType(
			HonkInit.idOf("feeder_level"),
			HoverDisplayBuilder.ComponentType.Visibility.ALWAYS,
			false,
			3
	);

	private static void feeder(HoverDisplayBuilder b) {
		if (b.getTarget().blockEntity() instanceof FeederBlockEntity feeder) {
			ItemStack stack = feeder.getStack(0);
			b.setComponent(FEEDER_LEVEL, WailaLike.getFeederTooltip(stack));
		}
	}

	@Override
	public void onInitialize() {
		HonkInit.LOGGER.info("Initializing Honk compatibility module for Polydex");

		HoverDisplayBuilder.register(HonkBlocks.FEEDER, PolydexCompatibility::feeder);
		HoverDisplayBuilder.register(HonkBlocks.CREATIVE_FEEDER, PolydexCompatibility::feeder);

		HoverDisplayBuilder.register(HonkEntities.HONK, (b) -> {
			if (b.getTarget().entity() instanceof HonkEntity honk) {
				b.setComponent(FOOD_LEVEL, WailaLike.getFoodLevelTooltip(honk));
				int breedingAge = honk.getBreedingAge();

				if (breedingAge < 0) {
					breedingAge = -breedingAge;
					b.setComponent(BREEDING_AGE, Text.translatable("text.honk.polydex.baby", String.format(WailaLike.TIMER, breedingAge / 20 / 60, breedingAge / 20 % 60)));
				} else if (breedingAge > 0) {
					b.setComponent(BREEDING_AGE, Text.translatable("text.honk.polydex.breeding_cooldown", String.format(WailaLike.TIMER, breedingAge / 20 / 60, breedingAge / 20 % 60)));
				}
			}
		});
		HoverDisplayBuilder.register(HonkEntities.EGG, (b) -> {
			if (b.getTarget().entity() instanceof EggEntity egg) {
				b.setComponent(EGG_WARMTH, WailaLike.getEggWarmthTooltip(egg));
				b.setComponent(EGG_AGE, WailaLike.getEggAgeTooltip(egg));
			}
		});
	}
}
