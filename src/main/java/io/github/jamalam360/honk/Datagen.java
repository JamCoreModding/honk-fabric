package io.github.jamalam360.honk;

import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.registry.HonkItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.MultipartBlockStateSupplier;
import net.minecraft.data.client.model.VariantSettings;
import net.minecraft.data.client.model.VariantSettings.Rotation;
import net.minecraft.data.client.model.When;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class Datagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dg) {
        dg.addProvider(ModelProvider::new);
    }

    private static class ModelProvider extends FabricModelProvider {

        public ModelProvider(FabricDataGenerator dg) {
            super(dg);
        }

        @Override
        public void generateItemModels(ItemModelGenerator gen) {
            gen.register(HonkItems.AMBER, Models.GENERATED);
            gen.register(HonkItems.EMPTY_SYRINGE, Models.HANDHELD);
            gen.register(HonkItems.BLOOD_SYRINGE, Models.HANDHELD);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator gen) {
            gen.registerNorthDefaultHorizontalRotation(HonkBlocks.CENTRIFUGE);
            gen.registerSimpleCubeAll(HonkBlocks.DEEPSLATE_AMBER_ORE);
        }
    }
}
