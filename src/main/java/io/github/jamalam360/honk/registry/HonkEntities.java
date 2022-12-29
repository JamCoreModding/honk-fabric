package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

@ContentRegistry(HonkInit.MOD_ID)
public class HonkEntities {

    public static final EntityType<EggEntity> EGG = QuiltEntityTypeBuilder
          .create(SpawnGroup.CREATURE, EggEntity::new)
          .setDimensions(EntityDimensions.fixed(6f / 16f, 6f / 16f))
          .build();
    public static final EntityType<HonkEntity> HONK = QuiltEntityTypeBuilder.<HonkEntity>create(SpawnGroup.CREATURE, (HonkEntity::new)).setDimensions(EntityDimensions.changing(0.5f, 0.5f)).build();

}
