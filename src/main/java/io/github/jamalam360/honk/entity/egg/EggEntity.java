package io.github.jamalam360.honk.entity.egg;

import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.item.EggItem;
import io.github.jamalam360.honk.registry.HonkEntities;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.util.WarmthHelper;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EggEntity extends MobEntity {

    private static final TrackedData<Integer> AGE = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COLD_TICKS = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<String> TYPE = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Optional<UUID>> PARENT = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Integer> GROWTH = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> PRODUCTIVITY = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> REPRODUCTIVITY = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> INSTABILITY = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> DENSITY = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);


    private boolean wobbling = false;
    private int wobbleTicks = 0;
    private boolean warm = false;
    private int lastWarmthCheck = 20;

    public EggEntity(EntityType<EggEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer createAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10F).build();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(AGE, 0);
        this.dataTracker.startTracking(COLD_TICKS, 0);
        this.dataTracker.startTracking(TYPE, "");
        this.dataTracker.startTracking(PARENT, Optional.empty());
        this.dataTracker.startTracking(GROWTH, 0);
        this.dataTracker.startTracking(PRODUCTIVITY, 0);
        this.dataTracker.startTracking(REPRODUCTIVITY, 0);
        this.dataTracker.startTracking(INSTABILITY, 0);
        this.dataTracker.startTracking(DENSITY, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.dataTracker.set(AGE, this.getAge() + 1);
        this.lastWarmthCheck++;

        if (this.getAge() > this.getMinimumHatchingAge() && this.warm) {
            if (this.world.random.nextFloat() < this.getHatchingChance()) {
                this.hatch();
            }

            if (wobbling) {
                wobbleTicks++;
                float pitchChange = (float) Math.sin(0.35 * wobbleTicks);
                this.setYaw(this.getYaw() + pitchChange);

                if (this.wobbleTicks >= 90) {
                    this.setYaw(0);
                    wobbling = false;
                }
            } else if (this.world.random.nextFloat() < 0.01F) {
                wobbling = true;
                wobbleTicks = 0;
                this.setPitch(1);
            }
        }

        if (this.lastWarmthCheck >= 20) {
            this.warm = WarmthHelper.isWarm(this.world, this.getBlockPos());

            if (this.warm) {
                this.dataTracker.set(COLD_TICKS, 0);
            }

            this.lastWarmthCheck = 0;
        }

        if (!this.warm) {
            this.dataTracker.set(COLD_TICKS, this.getColdTicks() + 1);
        }

        if (this.getColdTicks() > 600 && this.world.random.nextFloat() <= 0.01) {
            this.damage(DamageSource.GENERIC, 1);
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.world.isClient && player.isSneaking() && player.getStackInHand(hand).isEmpty()) {
            ItemStack egg = HonkItems.EGG.getDefaultStack();
            EggItem.initializeFrom(egg, this);
            player.setStackInHand(hand, egg);
            this.remove(RemovalReason.DISCARDED);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    public int getMinimumHatchingAge() {
        return 6000;
    }

    public float getHatchingChance() {
        return 0.0005f;
    }

    public void hatch() {
        if (!this.world.isClient) {
            HonkEntity spawned = HonkEntities.HONK.create(this.world);
            spawned.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            spawned.getDataTracker().set(HonkEntity.PARENT, this.dataTracker.get(PARENT));
            spawned.getDataTracker().set(HonkEntity.TYPE, this.dataTracker.get(TYPE));
            spawned.getDataTracker().set(HonkEntity.GROWTH, this.dataTracker.get(GROWTH));
            spawned.getDataTracker().set(HonkEntity.PRODUCTIVITY, this.dataTracker.get(PRODUCTIVITY));
            spawned.getDataTracker().set(HonkEntity.REPRODUCTIVITY, this.dataTracker.get(REPRODUCTIVITY));
            spawned.getDataTracker().set(HonkEntity.INSTABILITY, this.dataTracker.get(INSTABILITY));
            spawned.getDataTracker().set(HonkEntity.DENSITY, this.dataTracker.get(DENSITY));

            this.world.spawnEntity(spawned);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (fallDistance > 1f) {
            this.damage(DamageSource.FALL, this.getHealth());
        }

        return true;
    }

    @Override
    public int getSafeFallDistance() {
        return 1;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("honk:Age", this.dataTracker.get(AGE));
        nbt.putInt("honk:ColdTicks", this.dataTracker.get(COLD_TICKS));
        nbt.putString("honk:Type", this.dataTracker.get(TYPE));
        nbt.putString("honk:Parent", this.dataTracker.get(PARENT).map(UUID::toString).orElse(""));
        nbt.putInt("honk:Growth", this.dataTracker.get(GROWTH));
        nbt.putInt("honk:Productivity", this.dataTracker.get(PRODUCTIVITY));
        nbt.putInt("honk:Reproductivity", this.dataTracker.get(REPRODUCTIVITY));
        nbt.putInt("honk:Instability", this.dataTracker.get(INSTABILITY));
        nbt.putInt("honk:Density", this.dataTracker.get(DENSITY));
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(AGE, nbt.getInt("honk:Age"));
        this.dataTracker.set(COLD_TICKS, nbt.getInt("honk:ColdTicks"));
        this.dataTracker.set(TYPE, nbt.getString("honk:Type"));
        this.dataTracker.set(PARENT, Objects.equals(nbt.getString("honk:Parent"), "") ? Optional.empty() : Optional.of(UUID.fromString(nbt.getString("honk:Parent"))));
        this.dataTracker.set(GROWTH, nbt.getInt("honk:Growth"));
        this.dataTracker.set(PRODUCTIVITY, nbt.getInt("honk:Productivity"));
        this.dataTracker.set(REPRODUCTIVITY, nbt.getInt("honk:Reproductivity"));
        this.dataTracker.set(INSTABILITY, nbt.getInt("honk:Instability"));
        this.dataTracker.set(DENSITY, nbt.getInt("honk:Density"));
        super.readCustomDataFromNbt(nbt);
    }

    public int getAge() {
        return this.dataTracker.get(AGE);
    }

    public int getColdTicks() {
        return this.dataTracker.get(COLD_TICKS);
    }
}
