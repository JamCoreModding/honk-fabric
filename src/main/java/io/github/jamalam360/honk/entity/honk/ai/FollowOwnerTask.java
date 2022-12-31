package io.github.jamalam360.honk.entity.honk.ai;

import com.google.common.collect.ImmutableMap;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.VisibleLivingEntitiesCache;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalGetWithoutIsPresent"})
public class FollowOwnerTask extends Task<HonkEntity> {

    private final float maxDistanceSquared;
    private final Function<Pair<HonkEntity, LivingEntity>, Boolean> targetPredicate;
    private Optional<LivingEntity> target = Optional.empty();

    public FollowOwnerTask(Function<Pair<HonkEntity, LivingEntity>, Boolean> targetPredicate, float maxDistance) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
        this.maxDistanceSquared = maxDistance * maxDistance;
        this.targetPredicate = targetPredicate;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    protected boolean shouldRun(ServerWorld world, HonkEntity entity) {
        VisibleLivingEntitiesCache visibleLivingEntitiesCache = entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
        this.target = visibleLivingEntitiesCache.m_yzezovsk(
              (livingEntity ->
                    livingEntity.squaredDistanceTo(entity) <= (double) this.maxDistanceSquared && this.targetPredicate.apply(new Pair<>(entity, livingEntity))
              )
        );

        return target.isPresent();
    }

    @Override
    protected void run(ServerWorld world, HonkEntity entity, long time) {
        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.target.get(), true));
        this.target = Optional.empty();
    }
}
