package io.github.jamalam360.honk.entity.honk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.ai.HonkBrain;
import io.github.jamalam360.honk.registry.HonkEntities;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

@SuppressWarnings("unchecked")
public class HonkEntity extends PathAwareEntity {

    public static final TrackedData<String> TYPE = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Boolean> DISLIKES_OWNER = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Optional<UUID>> PARENT = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Integer> FOOD_LEVEL = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> EGG_TICKS = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> GROWTH = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> PRODUCTIVITY = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> REPRODUCTIVITY = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> INSTABILITY = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> DENSITY = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flapSpeed = 1.0F;

    /*
     * This constructor is for when we are generating a base honk entity.
     */
    public HonkEntity(EntityType<HonkEntity> type, World world) {
        this(
              type,
              world,
              HonkType.getRandom(1)
        );
    }

    public HonkEntity(EntityType<HonkEntity> type, World world, HonkType honkType) {
        super(type, world);
        this.setHonkType(honkType);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TYPE, "");
        this.dataTracker.startTracking(OWNER, Optional.empty());
        this.dataTracker.startTracking(DISLIKES_OWNER, false);
        this.dataTracker.startTracking(PARENT, Optional.empty());
        this.dataTracker.startTracking(FOOD_LEVEL, 0);
        this.dataTracker.startTracking(EGG_TICKS, -1);
        this.dataTracker.startTracking(GROWTH, 1);
        this.dataTracker.startTracking(PRODUCTIVITY, 1);
        this.dataTracker.startTracking(REPRODUCTIVITY, 1);
        this.dataTracker.startTracking(INSTABILITY, 1);
        this.dataTracker.startTracking(DENSITY, 1);
    }

    public static DefaultAttributeContainer createAttributes() {
        return MobEntity.createMobAttributes()
              .add(EntityAttributes.GENERIC_MAX_HEALTH, 28F)
              .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1F)
              .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32F)
              .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.2F)
              .build();
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return HonkBrain.create(HonkBrain.createProfile().deserialize(dynamic));
    }

    @Override
    public Brain<HonkEntity> getBrain() {
        return (Brain<HonkEntity>) super.getBrain();
    }

    @Override
    protected void mobTick() {
        Brain<HonkEntity> brain = this.getBrain();
        brain.tick((ServerWorld) this.world, this);

        Activity activity = brain.getFirstPossibleNonCoreActivity().orElse(null);

        if (activity != Activity.PANIC) {
            brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
            if (activity == Activity.FIGHT && brain.getFirstPossibleNonCoreActivity().orElse(null) != Activity.FIGHT) {
                brain.remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
            }
        }

        this.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));

        if (this.getFoodLevel() > 0 && this.world.random.nextFloat() < 0.01F) {
            this.setFoodLevel(this.getFoodLevel() - 1);
        } else if (this.getFoodLevel() == 0 && this.world.random.nextFloat() < 0.01F && this.getOwner().isPresent()) {
            this.damage(DamageSource.STARVE, 1);
        }

        if (this.getFoodLevel() >= 18) {
            if (this.world.random.nextFloat() < 0.01F) {
                this.setFoodLevel(this.getFoodLevel() - 1);
            }

            if (this.getEggTicks() == 0 && this.world.random.nextFloat() < 0.0015F) {
                if (!this.world.isClient) {
                    EggEntity entity = new EggEntity(HonkEntities.EGG, this.world);
                    entity.setPosition(this.getX(), this.getBlockY(), this.getZ());
                    this.initializeEggAttributes(entity);
                    this.world.spawnEntity(entity);
                    this.setEggTicks(-1);
                }
            } else if (this.getEggTicks() > 0) {
                this.setEggTicks(this.getEggTicks() - 1);
            }
        } else if (this.getEggTicks() > 0) {
            this.setEggTicks(-1);
        }
    }

    private void initializeEggAttributes(EggEntity entity) {
        entity.getDataTracker().set(EggEntity.TYPE, this.dataTracker.get(TYPE));
        entity.getDataTracker().set(EggEntity.PARENT, Optional.of(this.getUuid()));
        entity.getDataTracker().set(EggEntity.GROWTH, this.dataTracker.get(GROWTH) + generateMutation(this.world.random));
        entity.getDataTracker().set(EggEntity.PRODUCTIVITY, this.dataTracker.get(PRODUCTIVITY) + generateMutation(this.world.random));
        entity.getDataTracker().set(EggEntity.REPRODUCTIVITY, this.dataTracker.get(REPRODUCTIVITY) + generateMutation(this.world.random));
        entity.getDataTracker().set(EggEntity.INSTABILITY, this.dataTracker.get(INSTABILITY) + generateMutation(this.world.random));
        entity.getDataTracker().set(EggEntity.DENSITY, this.dataTracker.get(DENSITY) + generateMutation(this.world.random));
    }

    private int generateMutation(RandomGenerator random) {
        int instability = this.dataTracker.get(INSTABILITY);
        return random.range(-((instability / 10) / 2), (instability / 10) / 2);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);
        Brain<?> brain = this.getBrain();

        if (result) {
            brain.forget(MemoryModuleType.PATH);
            brain.forget(MemoryModuleType.WALK_TARGET);
            brain.forget(MemoryModuleType.LOOK_TARGET);
            brain.forget(MemoryModuleType.BREED_TARGET);
            brain.forget(MemoryModuleType.INTERACTION_TARGET);

            if (source.getAttacker() == null && source != DamageSource.STARVE) {
                brain.doExclusively(Activity.PANIC);
            } else if (source.getAttacker() != null && source.getAttacker().isPlayer()) {
                if (this.getOwner().map((o) -> source.getAttacker().getUuid().equals(o)).orElse(false)) {
                    this.dataTracker.set(DISLIKES_OWNER, true);
                }
            }

            if (source.getAttacker() != null) {
                brain.remember(MemoryModuleType.ATTACK_TARGET, (LivingEntity) source.getAttacker());
                brain.remember(MemoryModuleType.ANGRY_AT, source.getAttacker().getUuid());
            }
        }

        return result;
    }

    @Override
    public boolean onKilledOther(ServerWorld world, LivingEntity other) {
        if (this.dislikesOwner() && this.getOwner().map((o) -> o.equals(other.getUuid())).orElse(false)) {
            if (world.random.nextFloat() < 0.40F) {
                this.dataTracker.set(DISLIKES_OWNER, false);
            }
        }

        return super.onKilledOther(world, other);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation += (this.onGround ? -1.0F : 4.0F) * 0.3F;
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
        if (!this.onGround && this.flapSpeed < 1.0F) {
            this.flapSpeed = 1.0F;
        }

        this.flapSpeed *= 0.9F;
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }

        this.flapProgress += this.flapSpeed * 2.0F;
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (world.isClient) {
            if (MinecraftClient.getInstance().crosshairTarget.getType() == Type.ENTITY) {
                if (((EntityHitResult) MinecraftClient.getInstance().crosshairTarget).getEntity() == this) {
                    System.out.println(this.getFoodLevel());
                }
            }
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.getStackInHand(hand).isFood()) {
            FoodComponent component = player.getStackInHand(hand).getItem().getFoodComponent();
            this.setFoodLevel(this.getFoodLevel() + (component.isMeat() ? 3 : component.isSnack() ? 1 : 2));
            player.getStackInHand(hand).decrement(1);

            if (this.getFoodLevel() >= 18) {
                this.setEggTicks(1);
            }

            if (this.dislikesOwner() && component.isMeat() && this.getFoodLevel() >= 36 && this.world.random.nextFloat() <= 0.33F) {
                this.dataTracker.set(DISLIKES_OWNER, false);
            }

            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public double squaredAttackRange(LivingEntity target) {
        return 1.5D;
    }

    @Override
    protected boolean hasWings() {
        return true;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("honk:Type", this.dataTracker.get(TYPE));
        nbt.putString("honk:Owner", this.dataTracker.get(OWNER).map(UUID::toString).orElse(""));
        nbt.putBoolean("honk:DislikesOwner", this.dataTracker.get(DISLIKES_OWNER));
        nbt.putString("honk:Parent", this.dataTracker.get(PARENT).map(UUID::toString).orElse(""));
        nbt.putInt("honk:FoodLevel", this.dataTracker.get(FOOD_LEVEL));
        nbt.putInt("honk:EggTicks", this.dataTracker.get(EGG_TICKS));
        nbt.putInt("honk:Growth", this.dataTracker.get(GROWTH));
        nbt.putInt("honk:Productivity", this.dataTracker.get(PRODUCTIVITY));
        nbt.putInt("honk:Reproductivity", this.dataTracker.get(REPRODUCTIVITY));
        nbt.putInt("honk:Instability", this.dataTracker.get(INSTABILITY));
        nbt.putInt("honk:Density", this.dataTracker.get(DENSITY));
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(TYPE, nbt.getString("honk:Type"));
        this.dataTracker.set(OWNER, Objects.equals(nbt.getString("honk:Owner"), "") ? Optional.empty() : Optional.of(UUID.fromString(nbt.getString("honk:Owner"))));
        this.dataTracker.set(DISLIKES_OWNER, nbt.getBoolean("honk:DislikesOwner"));
        this.dataTracker.set(PARENT, Objects.equals(nbt.getString("honk:Parent"), "") ? Optional.empty() : Optional.of(UUID.fromString(nbt.getString("honk:Parent"))));
        this.dataTracker.set(FOOD_LEVEL, nbt.getInt("honk:FoodLevel"));
        this.dataTracker.set(EGG_TICKS, nbt.getInt("honk:EggTicks"));
        this.dataTracker.set(GROWTH, nbt.getInt("honk:Growth"));
        this.dataTracker.set(PRODUCTIVITY, nbt.getInt("honk:Productivity"));
        this.dataTracker.set(REPRODUCTIVITY, nbt.getInt("honk:Reproductivity"));
        this.dataTracker.set(INSTABILITY, nbt.getInt("honk:Instability"));
        this.dataTracker.set(DENSITY, nbt.getInt("honk:Density"));
        super.readCustomDataFromNbt(nbt);
    }

    /*
     * This was devised late at night by playing with Desmos.
     * Don't try at home.
     */
    public double getBlendedSizeModifier() {
        int growth = this.dataTracker.get(GROWTH);
        int density = this.dataTracker.get(DENSITY);
        Function<Integer, Double> calculateModifier = (x) -> Math.tan((x - 40) / 100D);

        return calculateModifier.apply(growth) + calculateModifier.apply(density);
    }

    public HonkType getHonkType() {
        return HonkType.ENTRIES.get(new Identifier(this.dataTracker.get(TYPE)));
    }

    public void setHonkType(HonkType type) {
        this.dataTracker.set(TYPE, Objects.requireNonNull(type.id()).toString());
    }

    public Optional<UUID> getOwner() {
        return this.dataTracker.get(OWNER);
    }

    public void setOwner(UUID owner) {
        this.dataTracker.set(OWNER, Optional.of(owner));
    }

    public boolean dislikesOwner() {
        return this.dataTracker.get(DISLIKES_OWNER);
    }

    public int getFoodLevel() {
        return this.dataTracker.get(FOOD_LEVEL);
    }

    public void setFoodLevel(int foodLevel) {
        this.dataTracker.set(FOOD_LEVEL, foodLevel);
    }

    public int getEggTicks() {
        return this.dataTracker.get(EGG_TICKS);
    }

    public void setEggTicks(int eggTicks) {
        this.dataTracker.set(EGG_TICKS, eggTicks);
    }
}
