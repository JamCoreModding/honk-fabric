/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.honk.entity.egg;

import io.github.jamalam360.honk.api.MagnifyingGlassInformationProvider;
import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.data.NbtKeys;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.item.EggItem;
import io.github.jamalam360.honk.registry.HonkEntities;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.util.Warmth;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EggEntity extends MobEntity implements MagnifyingGlassInformationProvider {

    public static final TrackedData<String> TYPE = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Optional<Integer>> PARENT = DataTracker.registerData(EggEntity.class, HonkEntities.OPTIONAL_INTEGER);
    public static final TrackedData<Integer> GROWTH = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> PRODUCTIVITY = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> REPRODUCTIVITY = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> INSTABILITY = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> AGE = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> COLD_TICKS = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public float wobbleAngle = 0;
    private boolean wobbling = false;
    private int wobbleTicks = 0;
    private boolean warm = false;
    private int prevWarmthCheck = 20;

    public EggEntity(EntityType<EggEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 16F);
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
    }

    @Override
    public void tick() {
        super.tick();
        this.dataTracker.set(AGE, this.getAge() + 1);
        this.prevWarmthCheck++;

        if (this.getAge() > this.getMinimumHatchingAge() && this.warm) {
            if (this.getWorld().random.nextFloat() < this.getHatchingChance()) {
                this.hatch();
            }
        }

        if (wobbling) {
            wobbleTicks++;
            float pitchChange = (float) Math.sin(0.35 * wobbleTicks - (11 * Math.PI) / 7);
            this.wobbleAngle = this.wobbleAngle + pitchChange;

            if (this.wobbleTicks >= 90) {
                this.wobbleAngle = 0;
                this.wobbling = false;
            }
        } else if (this.getAge() > 0.65 * this.getMinimumHatchingAge() && this.getWorld().random.nextFloat() < 0.01F) {
            this.wobbling = true;
            this.wobbleTicks = 0;
        }

        if (this.prevWarmthCheck >= 20) {
            this.warm = Warmth.isWarm(this.getWorld(), this.getBlockPos());

            if (this.warm) {
                this.dataTracker.set(COLD_TICKS, 0);
            }

            this.prevWarmthCheck = 0;
        }

        if (!this.warm) {
            this.dataTracker.set(COLD_TICKS, this.getColdTicks() + 1);
        }

        if (this.getColdTicks() > 600 && this.getWorld().random.nextFloat() <= 0.01) {
            this.damage(this.getDamageSources().generic(), 1);
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player.isSneaking() && player.getStackInHand(hand).isEmpty()) {
            ItemStack egg = HonkItems.EGG.getDefaultStack();
            EggItem.initializeFrom(egg, this);
            player.setStackInHand(hand, egg);
            this.remove(RemovalReason.DISCARDED);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    public int getMinimumHatchingAge() {
        return 400;
    }

    public float getHatchingChance() {
        return 0.05f;
    }

    public void hatch() {
        if (!this.getWorld().isClient) {
            HonkEntity spawned = HonkEntities.HONK.spawn((ServerWorld) this.getWorld(), this.getBlockPos(), SpawnReason.BREEDING);
            spawned.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            spawned.getDataTracker().set(HonkEntity.TYPE, this.dataTracker.get(TYPE));
            spawned.getDataTracker().set(HonkEntity.PARENT, this.dataTracker.get(PARENT));
            spawned.getDataTracker().set(HonkEntity.GROWTH, this.dataTracker.get(GROWTH));
            spawned.getDataTracker().set(HonkEntity.PRODUCTIVITY, this.dataTracker.get(PRODUCTIVITY));
            spawned.getDataTracker().set(HonkEntity.REPRODUCTIVITY, this.dataTracker.get(REPRODUCTIVITY));
            spawned.getDataTracker().set(HonkEntity.INSTABILITY, this.dataTracker.get(INSTABILITY));
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (fallDistance > 1f) {
            this.damage(this.getDamageSources().fall(), this.getHealth());
        }

        return true;
    }

    @Override
    public int getSafeFallDistance() {
        return 1;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt(NbtKeys.AGE, this.dataTracker.get(AGE));
        nbt.putInt(NbtKeys.COLD_TICKS, this.dataTracker.get(COLD_TICKS));
        this.dataTracker.get(PARENT).ifPresent((p) -> nbt.putInt(NbtKeys.PARENT, p));
        DnaData data = this.createDnaData();
        data.writeNbt(nbt);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(AGE, nbt.getInt(NbtKeys.AGE));
        this.dataTracker.set(COLD_TICKS, nbt.getInt(NbtKeys.COLD_TICKS));
        this.readDnaData(DnaData.fromNbt(nbt));

        if (nbt.contains(NbtKeys.PARENT)) {
            this.dataTracker.set(PARENT, Optional.of(nbt.getInt(NbtKeys.PARENT)));
        }

        super.readCustomDataFromNbt(nbt);
    }

    public void initializeBaseType() {
        HonkType type = HonkType.getRandom(1);
        this.dataTracker.set(AGE, 0);
        this.dataTracker.set(COLD_TICKS, 0);
        this.dataTracker.set(TYPE, type.id());
        this.dataTracker.set(GROWTH, 1);
        this.dataTracker.set(PRODUCTIVITY, 1);
        this.dataTracker.set(REPRODUCTIVITY, 1);
        this.dataTracker.set(INSTABILITY, 1);
    }

    public DnaData createDnaData() {
        return new DnaData(HonkType.ENTRIES.get(this.dataTracker.get(TYPE)), this.dataTracker.get(GROWTH), this.dataTracker.get(PRODUCTIVITY), this.dataTracker.get(REPRODUCTIVITY), this.dataTracker.get(INSTABILITY));
    }

    public void readDnaData(DnaData data) {
        if (data != null) {
            this.dataTracker.set(TYPE, data.type().id());
            this.dataTracker.set(GROWTH, data.growth());
            this.dataTracker.set(PRODUCTIVITY, data.productivity());
            this.dataTracker.set(REPRODUCTIVITY, data.reproductivity());
            this.dataTracker.set(INSTABILITY, data.instability());
        }
    }

    public int getAge() {
        return this.dataTracker.get(AGE);
    }

    public int getColdTicks() {
        return this.dataTracker.get(COLD_TICKS);
    }

    public void setGrowth(int value) {
        this.dataTracker.set(GROWTH, value);
    }

    public void setProductivity(int value) {
        this.dataTracker.set(PRODUCTIVITY, value);
    }

    public void setReproductivity(int value) {
        this.dataTracker.set(REPRODUCTIVITY, value);
    }

    public void setInstability(int value) {
        this.dataTracker.set(INSTABILITY, value);
    }

    @Override
    public List<Text> getMagnifyingGlassInformation(PlayerEntity user) {
        DnaData data = this.createDnaData();
        List<Text> info = data.getInformation();
        info.add(Text.literal(""));
        info.add(Text.translatable("text.honk.info_age", this.getAge()).styled(s -> s.withColor(Formatting.GRAY)));
        info.add(Text.translatable(this.warm ? "text.honk.info_warm_yes" : "text.honk.info_warm_no").styled(s -> s.withColor(Formatting.GRAY)));
        return info;
    }
}
