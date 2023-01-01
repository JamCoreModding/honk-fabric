/*
 * The MIT License (MIT)
 *
 * Copyright (c) [YEAR] Jamalam
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
import io.github.jamalam360.honk.data.recipe.DNAInjectorExtractorRecipe;
import io.github.jamalam360.honk.data.type.HonkTypeResourceReloadListener;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.registry.HonkCommands;
import io.github.jamalam360.honk.registry.HonkData;
import io.github.jamalam360.honk.registry.HonkEntities;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.registry.HonkScreens;
import io.github.jamalam360.honk.registry.HonkSensorTypes;
import io.github.jamalam360.honk.registry.HonkWorldGen;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.jamlib.registry.JamLibRegistry;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

public class HonkInit implements ModInitializer {

    public static final String MOD_ID = "honk";
    public static final JamLibLogger LOGGER = JamLibLogger.getLogger(MOD_ID);
    public static final QuiltItemGroup GROUP = QuiltItemGroup.create(id("group"));

    @Override
    public void onInitialize(ModContainer mod) {
        JamLibRegistry.register(HonkBlocks.class, HonkEntities.class, HonkItems.class, HonkScreens.class);
        GROUP.setIcon(HonkItems.BLOOD_SYRINGE);
        HonkCommands.init();
        HonkWorldGen.init();
        CentrifugeRecipe.init();
        DNAInjectorExtractorRecipe.init();
        HonkData.init();
        HonkSensorTypes.init();

        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(HonkEntities.EGG, EggEntity.createAttributes());
        DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.put(HonkEntities.HONK, HonkEntity.createAttributes());

        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(HonkTypeResourceReloadListener.INSTANCE);

        mod.metadata().value("compatibility_modules").asObject().forEach((modId, compatClass) -> {
            if (QuiltLoader.isModLoaded(modId)) {
                try {
                    Class<?> clazz = Class.forName("io.github.jamalam360.honk.compatibility." + compatClass.asString());
                    ModInitializer init = (ModInitializer) clazz.getConstructor().newInstance();
                    init.onInitialize(mod);
                } catch (Exception e) {
                    LOGGER.error("Failed to initialize compatibility module for mod " + modId + ": " + e);
                }
            }
        });

        LOGGER.logInitialize();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
