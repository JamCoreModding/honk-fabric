package io.github.jamalam360.honk.entity.egg;

import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.registry.HonkEntities;
import io.github.jamalam360.honk.util.WarmthHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class EggEntity extends MobEntity {

    private static final TrackedData<Integer> AGE = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COLD_TICKS = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private boolean wobbling = false;
    private int wobbleTicks = 0;
    private boolean warm = false;
    private int lastWarmthCheck = 20;

    public EggEntity(EntityType<EggEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(AGE, 0);
        this.dataTracker.startTracking(COLD_TICKS, 0);
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
            spawned.setBaby(true);
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

    public static DefaultAttributeContainer createAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10F).build();
    }

    public int getAge() {
        return this.dataTracker.get(AGE);
    }

    public int getColdTicks() {
        return this.dataTracker.get(COLD_TICKS);
    }
}
