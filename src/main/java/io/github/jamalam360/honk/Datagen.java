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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.jamalam360.honk.block.feeder.FeederBlock;
import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.registry.HonkWorldGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.*;
import net.minecraft.registry.HolderLookup;
import net.minecraft.registry.HolderLookup.Provider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistrySetBuilder;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.concurrent.CompletableFuture;

public class Datagen implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dg) {
		FabricDataGenerator.Pack pack = dg.createPack();
		pack.addProvider(ModelProvider::new);
		pack.addProvider(BlockLootTableProvider::new);
		pack.addProvider(TagProvider::new);
		pack.addProvider(WorldGenProvider::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(RegistryKeys.CONFIGURED_FEATURE, HonkWorldGen::getConfiguredFeatures);
		registryBuilder.add(RegistryKeys.PLACED_FEATURE, HonkWorldGen::getPlacedFeatures);
	}

	private static class ModelProvider extends FabricModelProvider {

		public ModelProvider(FabricDataOutput output) {
			super(output);
		}

		private static BlockStateVariantMap createNorthDefaultHorizontalRotationStates(Identifier model) {
			return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
					.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, model).put(VariantSettings.Y, VariantSettings.Rotation.R90))
					.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, model).put(VariantSettings.Y, VariantSettings.Rotation.R180))
					.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, model).put(VariantSettings.Y, VariantSettings.Rotation.R270))
					.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, model));
		}

		@Override
		public void generateItemModels(ItemModelGenerator gen) {
			gen.register(HonkItems.AMBER, Models.SINGLE_LAYER_ITEM);
			gen.register(HonkItems.EGG, Models.SINGLE_LAYER_ITEM);
			gen.register(HonkItems.FRIED_EGG, Models.SINGLE_LAYER_ITEM);
			gen.register(HonkItems.EMPTY_SYRINGE, Models.HANDHELD);
			gen.register(HonkItems.BLOOD_SYRINGE, Models.HANDHELD);
			gen.register(HonkItems.DNA, Models.SINGLE_LAYER_ITEM);
			gen.register(HonkItems.MAGNIFYING_GLASS, Models.HANDHELD);
			gen.register(HonkItems.SCREEN, Models.SINGLE_LAYER_ITEM);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator gen) {
			registerOrientableMachineWithBottom(gen, HonkBlocks.CENTRIFUGE);
			registerOrientableMachineWithBottom(gen, HonkBlocks.DNA_INJECTOR_EXTRACTOR);
			registerCombinator(gen, HonkBlocks.DNA_COMBINATOR);
			registerFeeder(gen, HonkBlocks.FEEDER);
			registerFeeder(gen, HonkBlocks.CREATIVE_FEEDER);
			gen.registerSimpleCubeAll(HonkBlocks.DEEPSLATE_AMBER_ORE);
			gen.registerSimpleCubeAll(HonkBlocks.MOD_LOGO);
		}

		public final void registerOrientableMachineWithBottom(BlockStateModelGenerator gen, Block machine) {
			Identifier identifier = TexturedModel.ORIENTABLE_WITH_BOTTOM.create(machine, gen.modelCollector);
			gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(machine).coordinate(createNorthDefaultHorizontalRotationStates(identifier)));
		}

		public final void registerCombinator(BlockStateModelGenerator gen, Block machine) {
			gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(machine).coordinate(createNorthDefaultHorizontalRotationStates(HonkInit.idOf("block/dna_combinator"))));
		}

		public final void registerFeeder(BlockStateModelGenerator gen, Block feeder) {
			gen.registerParentedItemModel(feeder, HonkInit.idOf("block/feeder"));
			gen.blockStateCollector
					.accept(
							MultipartBlockStateSupplier.create(feeder)
									.with(BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getId(feeder)))
									.with(When.create().set(FeederBlock.LEVEL, 1), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(feeder, "_contents1")))
									.with(When.create().set(FeederBlock.LEVEL, 2), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(feeder, "_contents2")))
									.with(When.create().set(FeederBlock.LEVEL, 3), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(feeder, "_contents3")))
									.with(When.create().set(FeederBlock.LEVEL, 4), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(feeder, "_contents4")))
									.with(When.create().set(FeederBlock.LEVEL, 5), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(feeder, "_contents5")))
					);

			for (int i = 1; i <= 5; i++) {
				registerFeederContentsModel(gen, feeder, i);
			}
		}

		public final void registerFeederContentsModel(BlockStateModelGenerator gen, Block feeder, int level) {
			// We have datagen at home!
			// datagen at home:
			gen.modelCollector.accept(Texture.getSubId(feeder, "_contents" + level), () -> {
				JsonObject textures = new JsonObject();
				textures.addProperty("particle", "honk:block/feeder_contents");
				textures.addProperty("inside", "honk:block/feeder_contents");
				JsonObject element = new JsonObject();
				JsonArray from = new JsonArray();
				JsonArray to = new JsonArray();
				JsonObject faces = new JsonObject();
				JsonObject up = new JsonObject();
				from.add(1);
				from.add(0);
				from.add(1);
				to.add(15);
				to.add(Math.round((level / 5F) * 10));
				to.add(15);
				up.addProperty("texture", "#inside");
				faces.add("up", up);
				element.add("from", from);
				element.add("to", to);
				element.add("faces", faces);
				JsonArray elements = new JsonArray();
				elements.add(element);
				JsonObject o = new JsonObject();
				o.add("textures", textures);
				o.add("elements", elements);
				return o;
			});
		}
	}

	private static class BlockLootTableProvider extends FabricBlockLootTableProvider {

		protected BlockLootTableProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generate() {
			this.addDrop(HonkBlocks.CENTRIFUGE);
			this.addDrop(HonkBlocks.DNA_INJECTOR_EXTRACTOR);
			this.addDrop(HonkBlocks.DNA_COMBINATOR);
			this.addDrop(HonkBlocks.MOD_LOGO);
			this.addDrop(HonkBlocks.FEEDER);
			this.addDrop(HonkBlocks.CREATIVE_FEEDER);
			this.addDrop(HonkBlocks.DEEPSLATE_AMBER_ORE, HonkItems.AMBER);
		}
	}

	private static class TagProvider extends BlockTagProvider {

		public TagProvider(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		public void configure(HolderLookup.Provider p) {
			this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
					.add(HonkBlocks.DEEPSLATE_AMBER_ORE)
					.add(HonkBlocks.DNA_INJECTOR_EXTRACTOR)
					.add(HonkBlocks.DNA_COMBINATOR)
					.add(HonkBlocks.CENTRIFUGE);
			this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(HonkBlocks.FEEDER);
			this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(HonkBlocks.CREATIVE_FEEDER);

			this.getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
					.add(HonkBlocks.DNA_INJECTOR_EXTRACTOR)
					.add(HonkBlocks.DNA_COMBINATOR)
					.add(HonkBlocks.CENTRIFUGE);
			this.getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(HonkBlocks.DEEPSLATE_AMBER_ORE);

			this.getOrCreateTagBuilder(BlockTags.WOOL).add(HonkBlocks.MOD_LOGO);
			this.getOrCreateTagBuilder(BlockTags.DAMPENS_VIBRATIONS).add(HonkBlocks.MOD_LOGO);
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	private static class WorldGenProvider extends FabricDynamicRegistryProvider {

		public WorldGenProvider(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(Provider registries, Entries entries) {
			entries.add(HonkWorldGen.AMBER_ORE_CONFIGURED_FEATURE_KEY, registries.getLookupOrThrow(RegistryKeys.CONFIGURED_FEATURE).getHolderOrThrow(HonkWorldGen.AMBER_ORE_CONFIGURED_FEATURE_KEY).value());
			entries.add(HonkWorldGen.AMBER_ORE_PLACED_FEATURE_KEY, registries.getLookupOrThrow(RegistryKeys.PLACED_FEATURE).getHolderOrThrow(HonkWorldGen.AMBER_ORE_PLACED_FEATURE_KEY).value());
		}

		@Override
		public String getName() {
			return "Honk World Gen";
		}
	}
}
