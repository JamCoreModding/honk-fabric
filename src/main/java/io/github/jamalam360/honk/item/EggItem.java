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

package io.github.jamalam360.honk.item;

import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.data.NbtKeys;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.registry.HonkEntities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.List;

public class EggItem extends Item {

	public EggItem() {
		super(new QuiltItemSettings().maxCount(1));
	}

	public static void initializeFrom(ItemStack stack, EggEntity entity) {
		NbtCompound nbt = stack.getOrCreateNbt();
		entity.writeCustomDataToNbt(nbt);
		stack.setNbt(nbt);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient) {
			EggEntity egg = new EggEntity(HonkEntities.EGG, context.getWorld());
			NbtCompound attributes = context.getStack().getOrCreateNbt();

			if (attributes == null || !attributes.contains(NbtKeys.DNA)) {
				egg.initializeBaseType();
			} else {
				egg.readCustomDataFromNbt(attributes);
			}

			egg.setPosition(context.getHitPos());
			context.getWorld().spawnEntity(egg);
			context.getPlayer().getStackInHand(context.getHand()).decrement(1);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		NbtCompound nbt = stack.getNbt();

		if (nbt == null || !nbt.contains(NbtKeys.DNA)) {
			tooltip.add(Text.literal("???").styled(s -> s.withItalic(true).withColor(Formatting.GRAY)));
			return;
		}

		DnaData data = DnaData.fromNbt(nbt);
		tooltip.addAll(data.getInformation());
		tooltip.add(Text.translatable("text.honk.info_age", nbt.getInt(NbtKeys.AGE)).styled(s -> s.withColor(Formatting.GRAY)));
	}
}
