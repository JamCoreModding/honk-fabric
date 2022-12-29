package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.util.Holder;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.InSquarePlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;

public class HonkWorldGen {

    private static final ConfiguredFeature<?, ?> AMBER_ORE_CONFIGURED_FEATURE = new ConfiguredFeature<>(
          Feature.ORE,
          new OreFeatureConfig(
                List.of(
                      OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, Blocks.YELLOW_WOOL.getDefaultState()),
                      OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.YELLOW_WOOL.getDefaultState())
                ),
                3
          )
    );

    public static PlacedFeature AMBER_ORE_PLACED_FEATURE = new PlacedFeature(
          Holder.createDirect(AMBER_ORE_CONFIGURED_FEATURE),
          List.of(
                CountPlacementModifier.create(BiasedToBottomIntProvider.create(0, 1)),
                InSquarePlacementModifier.getInstance(),
                HeightRangePlacementModifier.trapezoid(YOffset.getBottom(), YOffset.fixed(-54))
          )
    );

    public static void init() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, HonkInit.id("amber_ore"), AMBER_ORE_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, HonkInit.id("amber_ore"), AMBER_ORE_PLACED_FEATURE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, HonkInit.id("amber_ore")));
    }
}
