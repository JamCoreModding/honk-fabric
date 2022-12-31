package io.github.jamalam360.honk.entity.honk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.entity.honk.ai.HonkBrain;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@SuppressWarnings("unchecked")
public class HonkEntity extends PathAwareEntity {

    public static final TrackedData<String> TYPE = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Boolean> DISLIKES_OWNER = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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

            if (source.getAttacker() == null) {
                brain.doExclusively(Activity.PANIC);
            } else if (source.getAttacker().isPlayer()) {
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
                    System.out.println(this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET));
                }
            }
        }
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
}
