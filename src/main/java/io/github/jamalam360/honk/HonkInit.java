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

package io.github.jamalam360.honk;

import io.github.jamalam360.honk.data.recipe.CentrifugeRecipe;
import io.github.jamalam360.honk.data.recipe.DnaCombinatorRecipe;
import io.github.jamalam360.honk.data.recipe.DnaInjectorExtractorRecipe;
import io.github.jamalam360.honk.data.type.HonkTypeResourceReloadListener;
import io.github.jamalam360.honk.registry.*;
import io.github.jamalam360.honk.util.DatapackDependantItems;
import io.github.jamalam360.honk.util.HatchHonkCriterion;
import io.github.jamalam360.jamlib.compatibility.JamLibCompatibilityModuleHandler;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.jamlib.network.JamLibServerNetworking;
import io.github.jamalam360.jamlib.registry.JamLibRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

public class HonkInit implements ModInitializer {

	public static final String MOD_ID = "honk";
	public static final JamLibLogger LOGGER = JamLibLogger.getLogger(MOD_ID);
	public static final ItemGroup MAIN_GROUP = FabricItemGroup.builder().icon(HonkItems.BLOOD_SYRINGE::getDefaultStack).name(Text.translatable("group.honk.main")).build();
	public static final ItemGroup DNA_GROUP = FabricItemGroup.builder().icon(HonkItems.DNA::getDefaultStack).name(Text.translatable("group.honk.dna")).build();
	public static RegistryKey<ItemGroup> MAIN_GROUP_KEY;
	public static RegistryKey<ItemGroup> DNA_GROUP_KEY;

	public static Identifier idOf(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registries.ITEM_GROUP, idOf("main"), MAIN_GROUP);
		Registry.register(Registries.ITEM_GROUP, idOf("dna"), DNA_GROUP);
		MAIN_GROUP_KEY = Registries.ITEM_GROUP.getKey(MAIN_GROUP).get();
		DNA_GROUP_KEY = Registries.ITEM_GROUP.getKey(DNA_GROUP).get();

		ItemGroupEvents.modifyEntriesEvent(DNA_GROUP_KEY).register((entries -> {
			DatapackDependantItems.createDnaGroupStacks().forEach(entries::addStack);
		}));

		JamLibRegistry.register(HonkBlocks.class, HonkEntities.class, HonkItems.class, HonkScreens.class, HonkSounds.class);
		HonkWorldGen.init();
		HonkCommands.init();
		CentrifugeRecipe.init();
		DnaInjectorExtractorRecipe.init();
		DnaCombinatorRecipe.init();
		Criteria.register(HatchHonkCriterion.INSTANCE);
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(HonkTypeResourceReloadListener.INSTANCE);
		JamLibCompatibilityModuleHandler.initialize(MOD_ID);

		HonkC2SNetwork.HONK.setHandler(((server, player, handler, buf, responseSender) -> {
			// int rand = player.getRandom().nextInt(3);
			// SoundEvent ev = switch (rand) {
			// 	case 0 -> HonkSounds.HONK_AMBIENT;
			// 	case 1 -> HonkSounds.HONK_HURT;
			// 	case 2 ->
			// 			HonkSounds.APOLGY_FOR_BAD_ENGLISH_WHERE_WERE_U_WEN_HONK_DIE_I_WAS_AT_HOUSE_EATING_DORITO_WHEN_PHONE_RING_HONK_IS_KILL_NO;
			// 	default -> null;
			// };

			// float pitch = 1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F;

			// player.getWorld().playSound(null, player.getBlockPos(), ev, player.getSoundCategory(), 1.0F, pitch);
		}));
		JamLibServerNetworking.registerHandlers(MOD_ID);

		LOGGER.logInitialize();
		LOGGER.info("Honk!");
	}
}
