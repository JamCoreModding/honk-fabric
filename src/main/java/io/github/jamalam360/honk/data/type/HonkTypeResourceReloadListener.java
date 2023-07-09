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

package io.github.jamalam360.honk.data.type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.jamalam360.honk.HonkInit;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

public class HonkTypeResourceReloadListener implements SimpleSynchronousResourceReloader {

    public static final HonkTypeResourceReloadListener INSTANCE = new HonkTypeResourceReloadListener();
    private static final Gson GSON = new Gson();

    @Override
    public void reload(ResourceManager manager) {
        for (Entry<Identifier, Resource> entry : manager.findResources("honk_types", identifier -> identifier.getPath().endsWith(".json")).entrySet()) {
            Identifier resourceId = entry.getKey();
            Resource resource = entry.getValue();

            try (InputStream stream = resource.open()) {
                DataResult<HonkType> parseResult = HonkType.CODEC.codec().parse(JsonOps.INSTANCE, GSON.fromJson(new InputStreamReader(stream), JsonElement.class));

                if (parseResult.error().isEmpty()) {
                    String path = resourceId.getPath();
                    path = path.substring("honk_types/".length());
                    path = path.substring(0, path.length() - ".json".length());
                    HonkType.ENTRIES.put(
                          new Identifier(
                                resourceId.getNamespace(),
                                path
                          ).toString(),
                          parseResult.result().get()
                    );
                } else {
                    HonkInit.LOGGER.error("Failed to load type at path " + resourceId.toString() + " - " + parseResult.error().get().message());
                }
            } catch (Exception e) {
                HonkInit.LOGGER.error("Failed to load type at path " + resourceId.toString());
            }
        }

        HonkInit.LOGGER.info(String.format("Loaded %d types.", HonkType.ENTRIES.size()));
    }

    @Override
    public @NotNull Identifier getQuiltId() {
        return HonkInit.idOf("honk_type");
    }
}
