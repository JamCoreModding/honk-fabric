package io.github.jamalam360.honk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.jamalam360.honk.HonkInit;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.quiltmc.loader.api.QuiltLoader;

public class RegistryAnalysisDebug {

    private static final Path DUMP_DIRECTORY = QuiltLoader.getGameDir().resolve(".honk").resolve("registry-analysis");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static File analyseAndDump() throws IOException {
        HonkInit.LOGGER.info("Starting registry analysis...");
        long startTime = System.currentTimeMillis();
        List<Identifier> identifiers = new ArrayList<>();

        Registry.BLOCK.forEach((block) -> {
            Identifier id = Registry.BLOCK.getId(block);

            if (id.getPath().contains("ore")) {
                if (!(id.getPath().contains("deepslate") && identifiers.contains(new Identifier(id.toString().replace("deepslate_", ""))))) {
                    identifiers.add(id);
                }
            } else if (id.getPath().contains("ingot")) {
                identifiers.add(id);
            }
        });

        long elapsedTime = (System.currentTimeMillis() - startTime);
        HonkInit.LOGGER.info("Completed registry analysis in " + elapsedTime + "ms");

        if (!DUMP_DIRECTORY.toFile().exists()) {
            DUMP_DIRECTORY.toFile().mkdirs();
        }

        File f = DUMP_DIRECTORY.resolve("dump-" + new Date().toInstant().toString() + ".json").toFile();
        f.createNewFile();
        FileUtils.writeStringToFile(f, GSON.toJson(identifiers.stream().map(Identifier::toString).sorted().toList()), Charset.defaultCharset());
        return f;
    }
}
