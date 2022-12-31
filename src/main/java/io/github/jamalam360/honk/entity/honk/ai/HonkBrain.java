package io.github.jamalam360.honk.entity.honk.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.registry.HonkSensorTypes;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class HonkBrain {

    public static final ImmutableList<SensorType<? extends Sensor<? super HonkEntity>>> SENSORS = ImmutableList.of(
          SensorType.NEAREST_LIVING_ENTITIES,
          SensorType.NEAREST_PLAYERS,
          SensorType.HURT_BY,
          HonkSensorTypes.HONK_ATTACKABLES
    );
    public static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(MemoryModuleType.MOBS,
          MemoryModuleType.BREED_TARGET,
          MemoryModuleType.MOBS,
          MemoryModuleType.HURT_BY,
          MemoryModuleType.HURT_BY_ENTITY,
          MemoryModuleType.VISIBLE_MOBS,
          MemoryModuleType.NEAREST_VISIBLE_PLAYER,
          MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
          MemoryModuleType.LOOK_TARGET,
          MemoryModuleType.WALK_TARGET,
          MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
          MemoryModuleType.PATH,
          MemoryModuleType.ATTACK_TARGET,
          MemoryModuleType.ATTACK_COOLING_DOWN,
          MemoryModuleType.NEAREST_VISIBLE_ADULT,
          MemoryModuleType.NEAREST_ATTACKABLE,
          MemoryModuleType.HAS_HUNTING_COOLDOWN,
          MemoryModuleType.IS_PANICKING
    );

    public static Brain.Profile<HonkEntity> createProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    public static Brain<HonkEntity> create(Brain<HonkEntity> brain) {
        brain.setTaskList(
              Activity.CORE,
              0,
              ImmutableList.of(
                    new StayAboveWaterTask(0.6f),
                    new LookAroundTask(45, 90),
                    new ConditionalTask<>((e) -> e.world.random.nextFloat() < 0.01F, new JumpTask()),
                    new WanderAroundTask()
              )
        );

        brain.setTaskList(
              Activity.IDLE,
              ImmutableList.of(
                    Pair.of(0, new UpdateAttackTargetTask<>(HonkBrain::getAttackTarget)),
                    Pair.of(
                          1,
                          new RandomTask<>(
                                ImmutableMap.of(),
                                ImmutableList.of(
                                      Pair.of(new GoTowardsLookTarget(HonkBrain.getStalkSpeed(), 3), 2),
                                      Pair.of(new StrollTask(HonkBrain.getWalkSpeed()), 2),
                                      Pair.of(new TimeLimitedTask<>(new FollowOwnerTask((p) -> p.getLeft().getOwner().map((o) -> o.equals(p.getRight().getUuid())).orElse(false) && !p.getLeft().dislikesOwner(), 32), UniformIntProvider.create(160, 320)), 2),
                                      Pair.of(new WaitTask(30, 60), 1)
                                )
                          )
                    )
              ));

        brain.setTaskList(
              Activity.FIGHT,
              0,
              ImmutableList.of(
                    new StalkApproachTask(HonkBrain.getStalkSpeed(), HonkBrain.getRunSpeed()),
                    new MeleeAttackTask(40),
                    new ForgetAttackTargetTask<>((livingEntity -> livingEntity.world.random.nextFloat() < 0.001F))
              ),
              MemoryModuleType.ATTACK_TARGET
        );

        brain.setTaskList(
              Activity.PANIC,
              ImmutableList.of(
                    Pair.of(0, new StopPanickingTask()),
                    Pair.of(1, new RunFromNonEntityDamageTask(HonkBrain.getRunSpeed()))
              )
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();

        return brain;
    }

    private static float getWalkSpeed() {
        return 0.15F;
    }

    private static float getStalkSpeed() {
        return 0.08F;
    }

    private static float getRunSpeed() {
        return 0.28F;
    }

    private static Optional<? extends LivingEntity> getAttackTarget(HonkEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE);
    }
}
