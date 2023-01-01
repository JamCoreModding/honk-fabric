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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void reload(ResourceManager manager) {
        for (Entry<Identifier, Resource> entry : manager.findResources("honk_types", identifier -> identifier.getPath().endsWith(".json")).entrySet()) {
            Identifier resourceId = entry.getKey();
            Resource resource = entry.getValue();

            try (InputStream stream = resource.open()) {
                DataResult<HonkType> parseResult = HonkType.CODEC.codec().parse(JsonOps.INSTANCE, GSON.fromJson(new InputStreamReader(stream), JsonElement.class));

                if (parseResult.error().isEmpty()) {
                    HonkType.ENTRIES.put(
                          new Identifier(
                                resourceId.getNamespace(),
                                resourceId.getPath().substring("honk_types/".length()).substring(0, ".json".length())
                          ),
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
        return HonkInit.id("honk_type");
    }
}
