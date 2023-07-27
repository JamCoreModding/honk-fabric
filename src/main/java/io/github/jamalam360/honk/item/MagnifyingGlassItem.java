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

import io.github.jamalam360.honk.api.MagnifyingGlassInformationProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.List;

public class MagnifyingGlassItem extends Item {

	public MagnifyingGlassItem() {
		super(new QuiltItemSettings().maxCount(1));
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (!user.getWorld().isClient && entity instanceof MagnifyingGlassInformationProvider provider) {
			List<Text> info = provider.getMagnifyingGlassInformation(user);

			for (Text text : info) {
				user.sendMessage(text, false);
			}

			return ActionResult.SUCCESS;
		}

		return super.useOnEntity(stack, user, entity, hand);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient && context.getWorld().getBlockState(context.getBlockPos()) instanceof MagnifyingGlassInformationProvider provider) {
			List<Text> info = provider.getMagnifyingGlassInformation(context.getPlayer());

			for (Text text : info) {
				context.getPlayer().sendMessage(text, false);
			}

			return ActionResult.SUCCESS;
		}

		return super.useOnBlock(context);
	}
}
