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

package io.github.jamalam360.honk.compatibility;

import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WailaLike {
	public static final String TIMER = "%02d:%02d";

	public static Text getFoodLevelTooltip(HonkEntity honk) {
		return Text.literal("").append(Text.literal("\uD83C\uDF57 ").styled(s -> s.withColor(0x9F5A26)))
				.append(Text.literal(String.valueOf(honk.getFoodLevel())));
	}

	public static Text getEggWarmthTooltip(EggEntity egg) {
		if (egg.isWarm()) {
			return Text.literal("Warm");
		} else {
			return Text.literal("Cold").styled(s -> s.withColor(Formatting.GRAY));
		}
	}

	public static Text getEggAgeTooltip(EggEntity egg) {
		return Text.translatable("text.honk.waila.age", String.format(TIMER, egg.getAge() / 20 / 60, egg.getAge() / 20 % 60));
	}

	public static Text getFeederTooltip(ItemStack stack) {
		if (stack.isEmpty()) {
			return Text.literal("0");
		} else {
			return Text.translatable(stack.getTranslationKey()).append(Text.literal(" (" + stack.getCount() + "/" + stack.getMaxCount() + ")").styled(s -> s.withColor(Formatting.GRAY)));
		}
	}
}
