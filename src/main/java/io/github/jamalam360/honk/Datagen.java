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

import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.registry.HonkWorldGen;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.BlockStateVariantMap;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.TexturedModel;
import net.minecraft.data.client.model.VariantSettings;
import net.minecraft.data.client.model.VariantsBlockStateSupplier;
import net.minecraft.registry.HolderLookup;
import net.minecraft.registry.HolderLookup.Provider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistrySetBuilder;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class Datagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dg) {
        FabricDataGenerator.Pack pack = dg.createPack();
        pack.addProvider(ModelProvider::new);
        pack.addProvider(LootTableProvider::new);
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
            gen.register(HonkItems.EMPTY_SYRINGE, Models.HANDHELD);
            gen.register(HonkItems.BLOOD_SYRINGE, Models.HANDHELD);
            gen.register(HonkItems.DNA, Models.SINGLE_LAYER_ITEM);
            gen.register(HonkItems.MAGNIFYING_GLASS, Models.HANDHELD);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator gen) {
            registerTechnicallyOrientableMachine(gen, HonkBlocks.CENTRIFUGE);
            registerOrientableMachineWithBottom(gen, HonkBlocks.DNA_INJECTOR_EXTRACTOR);
            registerCombinator(gen, HonkBlocks.DNA_COMBINATOR);
            gen.registerSimpleCubeAll(HonkBlocks.DEEPSLATE_AMBER_ORE);
            gen.registerSimpleCubeAll(HonkBlocks.MOD_LOGO);
        }

        public final void registerOrientableMachineWithBottom(BlockStateModelGenerator gen, Block machine) {
            Identifier identifier = TexturedModel.ORIENTABLE_WITH_BOTTOM.create(machine, gen.modelCollector);
            gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(machine).coordinate(createNorthDefaultHorizontalRotationStates(identifier)));
        }

        public final void registerTechnicallyOrientableMachine(BlockStateModelGenerator gen, Block machine) {
            Identifier identifier = TexturedModel.CUBE_BOTTOM_TOP.create(machine, gen.modelCollector);
            gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(machine).coordinate(createNorthDefaultHorizontalRotationStates(identifier)));
        }

        public final void registerCombinator(BlockStateModelGenerator gen, Block machine) {
            gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(machine).coordinate(createNorthDefaultHorizontalRotationStates(HonkInit.idOf("block/dna_combinator"))));
        }
    }

    private static class LootTableProvider extends FabricBlockLootTableProvider {

        protected LootTableProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate() {
            this.addDrop(HonkBlocks.CENTRIFUGE);
            this.addDrop(HonkBlocks.DNA_INJECTOR_EXTRACTOR);
            this.addDrop(HonkBlocks.MOD_LOGO);
            this.addDrop(HonkBlocks.DEEPSLATE_AMBER_ORE, HonkItems.AMBER);
        }
    }

    private static class TagProvider extends BlockTagProvider {

        public TagProvider(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void configure(HolderLookup.Provider p) {
            this.getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(HonkBlocks.DEEPSLATE_AMBER_ORE);
            this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(HonkBlocks.DEEPSLATE_AMBER_ORE);
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
