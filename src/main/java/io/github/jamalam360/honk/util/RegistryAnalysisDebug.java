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
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.quiltmc.loader.api.QuiltLoader;

public class RegistryAnalysisDebug {

    private static final Path DUMP_DIRECTORY = QuiltLoader.getGameDir().resolve(".honk").resolve("registry-analysis");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static File analyseAndDump() throws IOException {
        HonkInit.LOGGER.info("Starting registry analysis...");
        long startTime = System.currentTimeMillis();
        List<Identifier> identifiers = new ArrayList<>();

        Registries.BLOCK.forEach((block) -> {
            Identifier id = Registries.BLOCK.getId(block);

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
