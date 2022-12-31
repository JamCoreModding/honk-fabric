package io.github.jamalam360.honk.entity.honk.ai;

import com.google.common.collect.ImmutableMap;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class StopPanickingTask extends Task<HonkEntity> {

    public StopPanickingTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected void run(ServerWorld serverWorld, HonkEntity entity, long l) {
        if (!(RunFromNonEntityDamageTask.wasHurt(entity) || wasHurtByNearbySource(entity))) {
            entity.getBrain().forget(MemoryModuleType.HURT_BY);
            entity.getBrain().forget(MemoryModuleType.HURT_BY_ENTITY);
            entity.getBrain().resetPossibleActivities();
        }
    }

    private static boolean wasHurtByNearbySource(HonkEntity entity) {
        return entity.getBrain()
              .getOptionalMemory(MemoryModuleType.HURT_BY)
              .filter(source -> source.getPosition().squaredDistanceTo(entity.getPos()) <= 36.0)
              .isPresent();
    }
}
