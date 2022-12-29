package io.github.jamalam360.honk.registry;

import static net.minecraft.server.command.CommandManager.literal;

import io.github.jamalam360.honk.data.type.HonkType;
import java.util.Comparator;
import net.minecraft.text.Text;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

public class HonkCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, buildContext, environment) -> dispatcher.register(
              literal("honk-debug")
                    .then(literal("dump-types").executes((context) -> {
                        context.getSource().sendFeedback(Text.literal(HonkType.ENTRIES.size() + " types registered."), false);

                        HonkType.ENTRIES
                              .entrySet()
                              .stream()
                              .sorted(Comparator.comparing(o -> o.getKey().toString()))
                              .forEach(((entry) -> context.getSource().sendFeedback(
                                    Text.literal(
                                          String.format(
                                                "%s (Tier %d): %s",
                                                entry.getKey().toString(),
                                                entry.getValue().tier(),
                                                entry.getValue().output().toString()
                                          )
                                    ), false
                              )));

                        return 0;
                    }))
        )));
    }
}
