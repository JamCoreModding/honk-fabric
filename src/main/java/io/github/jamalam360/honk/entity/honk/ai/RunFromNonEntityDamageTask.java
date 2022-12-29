package io.github.jamalam360.honk.entity.honk.ai;

import java.util.Optional;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;

public class RunFromNonEntityDamageTask extends StrollTask {

    public RunFromNonEntityDamageTask(float speed) {
        super(speed);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        return wasHurt(pathAwareEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, PathAwareEntity entity, long time) {
        return wasHurt(entity) && time < 260L;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        super.run(serverWorld, pathAwareEntity, l);

        if (wasHurt(pathAwareEntity)) {
            Brain<?> brain = pathAwareEntity.getBrain();
            if (!brain.hasActivity(Activity.PANIC)) {
                brain.forget(MemoryModuleType.PATH);
                brain.forget(MemoryModuleType.WALK_TARGET);
                brain.forget(MemoryModuleType.LOOK_TARGET);
                brain.forget(MemoryModuleType.BREED_TARGET);
                brain.forget(MemoryModuleType.INTERACTION_TARGET);
            }

            brain.doExclusively(Activity.PANIC);
        }
    }

    public static boolean wasHurt(PathAwareEntity entity){
        Optional<DamageSource> latestDamage = entity.getBrain().getOptionalMemory(MemoryModuleType.HURT_BY);
        return latestDamage.isPresent() && latestDamage.get().getAttacker() == null;
    }
}
