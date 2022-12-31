package io.github.jamalam360.honk.entity.honk.ai;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class StalkApproachTask extends Task<MobEntity> {

    private final float stalkSpeed;
    private final float attackSpeed;

    public StalkApproachTask(float stalkSpeed, float attackSpeed) {
        super(
              ImmutableMap.of(
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleState.REGISTERED,
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleState.REGISTERED,
                    MemoryModuleType.ATTACK_TARGET,
                    MemoryModuleState.VALUE_PRESENT,
                    MemoryModuleType.VISIBLE_MOBS,
                    MemoryModuleState.REGISTERED
              )
        );

        this.stalkSpeed = stalkSpeed;
        this.attackSpeed = attackSpeed;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, MobEntity entity, long time) {
        Optional<LivingEntity> optionalMemory = entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        return optionalMemory.map((livingEntity) -> LookTargetUtil.isVisibleInMemory(entity, livingEntity) && LookTargetUtil.isTargetWithinAttackRange(entity, livingEntity, 1)).orElse(false);
    }

    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        LivingEntity livingEntity = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.isTargetWithinAttackRange(mobEntity, livingEntity, 1)) {
            this.forgetWalkTarget(mobEntity);
        } else {
            this.rememberWalkTarget(mobEntity, livingEntity);
        }
    }

    private void rememberWalkTarget(LivingEntity entity, LivingEntity target) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));

        double distance = entity.squaredDistanceTo(target);
        float speed = this.stalkSpeed;

        if (distance < 120 || entity.world.random.nextFloat() < 0.02F) {
            speed = this.attackSpeed;
        }

        WalkTarget walkTarget = new WalkTarget(new EntityLookTarget(target, false), speed, 0);
        brain.remember(MemoryModuleType.WALK_TARGET, walkTarget);
    }

    private void forgetWalkTarget(LivingEntity entity) {
        entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }
}
