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
