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

import static net.minecraft.server.command.CommandManager.literal;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.util.RegistryAnalysisDebug;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import net.minecraft.text.Text;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

public class HonkCommands {

    public static void init() {
        if (QuiltLoader.isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register(((dispatcher, buildContext, environment) -> dispatcher.register(
                  literal("honk-debug")
                        .requires((source) -> source.hasPermissionLevel(4))
                        .then(literal("dump-types").executes((context) -> {
                            context.getSource().sendFeedback(() -> Text.literal(HonkType.ENTRIES.size() + " types registered."), false);

                            HonkType.ENTRIES
                                  .entrySet()
                                  .stream()
                                  .sorted(Entry.comparingByKey())
                                  .forEach(((entry) -> context.getSource().sendFeedback(
                                        () -> Text.literal(
                                              String.format(
                                                    "%s (Tier %d): %s",
                                                    entry.getKey(),
                                                    entry.getValue().tier(),
                                                    entry.getValue().output().toString()
                                              )
                                        ), false
                                  )));

                            return 0;
                        }))
                        .then(literal("analyse-registry").executes((context) -> {
                            try {
                                File f = RegistryAnalysisDebug.analyseAndDump();
                                context.getSource().sendFeedback(() -> Text.literal("Wrote to file " + f), false);
                                return 0;
                            } catch (IOException e) {
                                context.getSource().sendFeedback(() -> Text.literal("Failed to create dump"), false);
                                HonkInit.LOGGER.error(e.toString());
                                return 1;
                            }

                        }))
            )));
        }
    }
}
