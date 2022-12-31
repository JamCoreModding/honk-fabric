package io.github.jamalam360.honk.registry;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import java.util.Comparator;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

public class HonkCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, buildContext, environment) -> dispatcher.register(
              literal("honk-debug")
                    .requires((source) -> source.hasPermissionLevel(4))
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
                    .then(literal("take-ownership").executes((context) -> {
                        List<HonkEntity> entities = context.getSource().getWorld().getEntitiesByClass(HonkEntity.class, Box.of(context.getSource().getPosition(), 10, 10, 10), (entity) -> true);
                        entities.forEach((e) -> {
                            try {
                                e.setOwner(context.getSource().getPlayer().getUuid());
                            } catch (CommandSyntaxException ex) {
                                HonkInit.LOGGER.error(ex.toString());
                            }
                        });

                        context.getSource().sendFeedback(Text.literal("Took ownership of " + entities.size() + " entities."), false);

                        return 0;
                    }))
        )));
    }
}
