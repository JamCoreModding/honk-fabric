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

package io.github.jamalam360.honk.registry;

import java.util.List;
import net.minecraft.registry.HolderProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.world.gen.BootstrapContext;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.BiomePlacementModifier;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.InSquarePlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.ConfiguredFeatureUtil;
import net.minecraft.world.gen.feature.util.PlacedFeatureUtil;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;

public class HonkWorldGen {

    public static final RegistryKey<ConfiguredFeature<?, ?>> AMBER_ORE_CONFIGURED_FEATURE_KEY = ConfiguredFeatureUtil.getRegistryKey("honk_ore_amber");
    public static final OreFeatureConfig AMBER_ORE_CONFIG = new OreFeatureConfig(List.of(OreFeatureConfig.createTarget(new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), HonkBlocks.DEEPSLATE_AMBER_ORE.getDefaultState())), 1);
    public static final RegistryKey<PlacedFeature> AMBER_ORE_PLACED_FEATURE_KEY = PlacedFeatureUtil.createRegistryKey("honk_ore_amber");

    public static void init() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, AMBER_ORE_PLACED_FEATURE_KEY);
    }

    public static void getConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> bootstrap) {
        bootstrap.register(AMBER_ORE_CONFIGURED_FEATURE_KEY, new ConfiguredFeature<>(Feature.ORE, AMBER_ORE_CONFIG));
    }

    public static void getPlacedFeatures(BootstrapContext<PlacedFeature> bootstrap) {
        HolderProvider<ConfiguredFeature<?, ?>> provider = bootstrap.lookup(RegistryKeys.CONFIGURED_FEATURE);
        bootstrap.register(AMBER_ORE_PLACED_FEATURE_KEY, new PlacedFeature(provider.getHolderOrThrow(AMBER_ORE_CONFIGURED_FEATURE_KEY), List.of(CountPlacementModifier.create(240), InSquarePlacementModifier.getInstance(), HeightRangePlacementModifier.trapezoid(YOffset.fixed(0), YOffset.BOTTOM), BiomePlacementModifier.getInstance())));
    }
}
