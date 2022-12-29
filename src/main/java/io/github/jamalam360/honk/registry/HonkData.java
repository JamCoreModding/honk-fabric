package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.data.rea.EntityBloodData;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;

public class HonkData {

    public static final RegistryEntryAttachment<EntityType<?>, EntityBloodData> ENTITY_BLOOD_DATA = RegistryEntryAttachment.builder(
          Registry.ENTITY_TYPE,
          HonkInit.id("blood_data"),
          EntityBloodData.class,
          EntityBloodData.CODEC.codec()
    ).defaultValue(new EntityBloodData(Optional.of(0.2F), Optional.of(true), Optional.empty(), Optional.empty())).build();

    public static void init() {
        HonkInit.LOGGER.info("Registered data entries");
    }
}
