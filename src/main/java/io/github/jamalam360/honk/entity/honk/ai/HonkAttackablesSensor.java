package io.github.jamalam360.honk.entity.honk.ai;

import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;

public class HonkAttackablesSensor extends NearestVisibleLivingEntitySensor {

    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        if (entity instanceof HonkEntity thisHonk && target instanceof HonkEntity otherHonk) {
            return !thisHonk.getHonkType().equals(otherHonk.getHonkType());
        }

        if (entity.getAttacker() == target) {
            return !(target instanceof PlayerEntity) || entity.world.getDifficulty() != Difficulty.PEACEFUL;
        }

        if (entity instanceof HonkEntity honk) {
            if (honk.dislikesOwner() && honk.getOwner().map((o) -> o.equals(target.getUuid())).orElse(false)) {
                return true;
            }
        }

        return !(target instanceof HonkEntity || target instanceof EggEntity) && entity.world.random.nextFloat() < 0.05f;
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
