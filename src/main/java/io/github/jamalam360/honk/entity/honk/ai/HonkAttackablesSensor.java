package io.github.jamalam360.honk.entity.honk.ai;

import io.github.jamalam360.honk.entity.honk.HonkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;

public class HonkAttackablesSensor extends NearestVisibleLivingEntitySensor {

    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        if (entity instanceof HonkEntity thisHonk && target instanceof HonkEntity otherHonk) {
            return !thisHonk.getHonkType().equals(otherHonk.getHonkType());
        }

        if (entity.getAttacker() == target) {
            return true;
        }

        return !(target instanceof HonkEntity) && entity.world.random.nextFloat() < 0.05f;
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
