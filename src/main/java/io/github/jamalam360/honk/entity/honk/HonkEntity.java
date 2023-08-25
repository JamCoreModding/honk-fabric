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

package io.github.jamalam360.honk.entity.honk;

import com.mojang.datafixers.util.Pair;
import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.api.MagnifyingGlassInformationProvider;
import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.data.NbtKeys;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.ai.*;
import io.github.jamalam360.honk.registry.HonkEntities;
import io.github.jamalam360.honk.registry.HonkSounds;
import io.github.jamalam360.honk.util.DnaCombinator;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.int_provider.BiasedToBottomIntProvider;
import net.minecraft.util.math.int_provider.IntProvider;
import net.minecraft.util.math.int_provider.UniformIntProvider;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class HonkEntity extends AnimalEntity implements MagnifyingGlassInformationProvider, Angerable {

	public static final TrackedData<String> TYPE = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.STRING);
	public static final TrackedData<Optional<Integer>> PARENT = DataTracker.registerData(HonkEntity.class, HonkEntities.OPTIONAL_INTEGER);
	public static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Integer> FOOD_LEVEL = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Integer> GROWTH = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Integer> PRODUCTIVITY = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Integer> REPRODUCTIVITY = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Integer> INSTABILITY = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Boolean> HUNGER_DISABLED = DataTracker.registerData(HonkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
	private static final IntProvider DROP_COUNT = BiasedToBottomIntProvider.create(1, 3);
	public float flapProgress;
	public float prevFlapProgress;
	public float maxWingDeviation;
	public float prevMaxWingDeviation;
	public float flapSpeed = 1.0F;
	public UUID targetUuid;

	public HonkEntity(EntityType<HonkEntity> type, World world) {
		super(type, world);

		if (!world.isClient) {
			this.setHonkType(HonkType.getRandom(1));
			this.setFoodLevel(40);
		}
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 28F).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32F).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.2F).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TYPE, new Identifier("honk", "white").toString());
		this.dataTracker.startTracking(PARENT, Optional.empty());
		this.dataTracker.startTracking(ANGER_TIME, 0);
		this.dataTracker.startTracking(FOOD_LEVEL, 40);
		this.dataTracker.startTracking(GROWTH, 1);
		this.dataTracker.startTracking(PRODUCTIVITY, 1);
		this.dataTracker.startTracking(REPRODUCTIVITY, 1);
		this.dataTracker.startTracking(INSTABILITY, 1);
		this.dataTracker.startTracking(HUNGER_DISABLED, false);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new TemptHonkGoal(this, 1.1F, false));
		this.goalSelector.add(1, new AnimalMateGoal(this, 1.0F));
		this.goalSelector.add(2, new EscapeDangerIfTooHurtGoal(this, 1.5F));
		this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2F, true));
		this.goalSelector.add(3, new FindHonkFeederGoal(this, 1.1F));
		this.goalSelector.add(4, new FollowHonkParentGoal(this, 1.4F));
		this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0F));
		this.goalSelector.add(4, new HonkMoveIntoWaterGoal(this));
		this.goalSelector.add(4, new HonkMoveOnTopOfWarmBlockGoal(this));
		this.goalSelector.add(5, new MoveTowardsOtherHonksOfSameTypeGoal(this, 0.8F));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));

		this.targetSelector.add(1, new RevengeWithoutUnviersalAngerCheckGoal(this));
		this.targetSelector.add(2, new TargetGoal<>(this, HonkEntity.class, 10, true, true, (entity) -> entity instanceof HonkEntity honk && honk.getHonkType() != this.getHonkType()));
	}

	//region Ticking
	@Override
	protected void mobTick() {
		super.mobTick();

		if (!this.isAiDisabled() && !this.dataTracker.get(HUNGER_DISABLED)) {
			if (this.getFoodLevel() > 0 && this.getWorld().random.nextFloat() < (0.005F) * this.getTierOrDefault()) {
				this.setFoodLevel(this.getFoodLevel() - 1);
			} else if (this.getFoodLevel() == 0 && this.getWorld().random.nextFloat() < 0.01F) {
				this.damage(this.getDamageSources().starve(), 1);
			}

			if (this.getFoodLevel() > 0 && this.getHealth() < this.getMaxHealth() && this.age % 50 == 0) {
				this.heal(1.0F);
				this.setFoodLevel(this.getFoodLevel() - 1);
			}
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();

		this.prevFlapProgress = this.flapProgress;
		this.prevMaxWingDeviation = this.maxWingDeviation;
		this.maxWingDeviation += (this.isOnGround() ? -1.0F : 4.0F) * 0.3F;
		this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
		if (!this.isOnGround() && this.flapSpeed < 1.0F) {
			this.flapSpeed = 1.0F;
		}

		this.flapSpeed *= 0.9F;
		Vec3d v = this.getVelocity();
		if (!this.isOnGround() && v.y < 0.0) {
			this.setVelocity(v.multiply(1.0, 0.6, 1.0));
		}

		this.flapProgress += this.flapSpeed * 2.0F;
	}
	//endregion

	//region Eating & Breeding
	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (this.isAiDisabled()) return ActionResult.PASS;

		ItemStack itemStack = player.getStackInHand(hand);

		if (this.isBreedingItem(itemStack)) {
			int i = this.getBreedingAge();
			if (!this.getWorld().isClient && this.canEat()) {
				this.eat(player, hand, itemStack);

				if (i == 0) {
					this.lovePlayer(player);
				}

				return ActionResult.SUCCESS;
			}

			if (this.isBaby()) {
				this.eat(player, hand, itemStack);
				this.growUp(getGrowthSeconds(-i), true);
				return ActionResult.success(this.getWorld().isClient);
			}

			if (this.getWorld().isClient) {
				return ActionResult.CONSUME;
			}
		}

		return super.interactMob(player, hand);
	}

	@Override
	protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
		super.eat(player, hand, stack);

		if (player.getStackInHand(hand).isFood()) {
			this.eatHonkFood(stack);
		}
	}

	public void eatHonkFood(ItemStack stack) {
		if (this.getFoodLevel() < 50) {
			FoodComponent component = stack.getItem().getFoodComponent();
			this.setFoodLevel(this.getFoodLevel() + (component.isMeat() ? 3 : component.isSnack() ? 1 : 2));

			this.getWorld().playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					this.getEatSound(stack),
					SoundCategory.NEUTRAL,
					1.0F,
					1.0F + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.4F
			);

			if (!this.getWorld().isClient) {
				for (Pair<StatusEffectInstance, Float> pair : stack.getItem().getFoodComponent().getStatusEffects()) {
					if (pair.getFirst() != null && this.getWorld().random.nextFloat() < pair.getSecond()) {
						this.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
					}
				}
			}

			this.emitGameEvent(GameEvent.EAT);
		}
	}

	@Override
	public void breed(ServerWorld world, AnimalEntity aOther) {
		HonkEntity other = (HonkEntity) aOther;
		EggEntity egg = HonkEntities.EGG.create(world);
		egg.setPos(this.getX(), this.getY() + 1, this.getZ());
		egg.setParent(this);
		Objects.requireNonNull(DnaCombinator.combine(world.getRandom(), this.createDnaData(), other.createDnaData())).writeEntity(egg);
		egg.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);

		this.setBreedingAge(-400 * this.getReproductivity() + 6400);
		other.setBreedingAge(-400 * other.getReproductivity() + 6400);
		this.resetLoveTicks();
		other.resetLoveTicks();
		world.sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);
		if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
			world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
		}

		world.spawnEntityAndPassengers(egg);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isFood();
	}

	@Override
	public boolean canEat() {
		return this.getFoodLevel() < 50;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return other instanceof HonkEntity otherHonk && this.getHonkType() == otherHonk.getHonkType() && this.isInLove() && other.isInLove();
	}

	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity pOther) {
		return null;
	}
	//endregion

	//region Audio
	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return HonkSounds.HONK_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return HonkSounds.APOLGY_FOR_BAD_ENGLISH_WHERE_WERE_U_WEN_HONK_DIE_I_WAS_AT_HOUSE_EATING_DORITO_WHEN_PHONE_RING_HONK_IS_KILL_NO;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return HonkSounds.HONK_AMBIENT;
	}
	//endregion

	//region Anger
	@Override
	public int getAngerTime() {
		return this.dataTracker.get(ANGER_TIME);
	}

	@Override
	public void setAngerTime(int ticks) {
		this.dataTracker.set(ANGER_TIME, ticks);
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
	}

	@Nullable
	@Override
	public UUID getAngryAt() {
		return this.targetUuid;
	}

	@Override
	public void setAngryAt(@Nullable UUID uuid) {
		this.targetUuid = uuid;
	}
	//endregion

	//region NBT & DNA
	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt(NbtKeys.FOOD_LEVEL, this.dataTracker.get(FOOD_LEVEL));
		nbt.putBoolean(NbtKeys.NO_HUNGER, this.dataTracker.get(HUNGER_DISABLED));
		this.dataTracker.get(PARENT).ifPresent((p) -> nbt.putInt(NbtKeys.PARENT, p));
		DnaData data = this.createDnaData();
		data.writeNbt(nbt);
		super.writeCustomDataToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.dataTracker.set(FOOD_LEVEL, nbt.getInt(NbtKeys.FOOD_LEVEL));
		this.dataTracker.set(HUNGER_DISABLED, nbt.getBoolean(NbtKeys.NO_HUNGER));
		this.readDnaData(DnaData.fromNbt(nbt));

		if (nbt.contains(NbtKeys.PARENT)) {
			this.dataTracker.set(PARENT, Optional.of(nbt.getInt(NbtKeys.PARENT)));
		}

		super.readCustomDataFromNbt(nbt);
	}

	public DnaData createDnaData() {
		if (this.getHonkType() == null) {
			HonkInit.LOGGER.warn("Creating DNA data with a null type; this is concerning.");
		}

		return new DnaData(this.getHonkType(), this.dataTracker.get(GROWTH), this.dataTracker.get(PRODUCTIVITY), this.dataTracker.get(REPRODUCTIVITY), this.dataTracker.get(INSTABILITY));
	}

	public void readDnaData(DnaData data) {
		if (data != null && data.type() != null) {
			this.dataTracker.set(TYPE, data.type().id());
			this.dataTracker.set(GROWTH, data.growth());
			this.dataTracker.set(PRODUCTIVITY, data.productivity());
			this.dataTracker.set(REPRODUCTIVITY, data.reproductivity());
			this.dataTracker.set(INSTABILITY, data.instability());
		}
	}
	//endregion

	//region Magnifying Glass
	@Override
	public List<Text> getMagnifyingGlassInformation(PlayerEntity user) {
		DnaData data = this.createDnaData();
		return data.getInformation();
	}
	//endregion

	//region Entity Boilerplate
	@Override
	protected void tickStatusEffects() {
		super.tickStatusEffects();

		if (this.hasStatusEffect(StatusEffects.HUNGER) && this.getWorld().random.nextFloat() < (0.04F * this.getStatusEffect(StatusEffects.HUNGER).getAmplifier()) * 20) {
			this.setFoodLevel(Math.max(0, this.getFoodLevel() - 1));
		}
	}

	@Override
	public Text getDefaultName() {
		if (this.getHonkType() == null) {
			return super.getDefaultName();
		} else {
			return MutableText.create(super.getDefaultName().asComponent()).append(" (").append(Text.translatable(this.getHonkType().name())).append(")");
		}
	}

	@Override
	public double getAttributeValue(EntityAttribute attribute) {
		if (this.getHonkType() == null) {
			return super.getAttributeValue(attribute);
		}

		if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
			return this.getAttributes().getValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + this.getHonkType().tier();
		} else if (attribute == EntityAttributes.GENERIC_ATTACK_KNOCKBACK) {
			return this.getAttributes().getValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK) + this.getHonkType().tier();
		} else if (attribute == EntityAttributes.GENERIC_MAX_HEALTH) {
			return this.getAttributes().getValue(EntityAttributes.GENERIC_MAX_HEALTH) + this.getHonkType().tier() * 4;
		} else if (attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED) {
			return this.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + this.getHonkType().tier() / 50F;
		} else {
			return super.getAttributeValue(attribute);
		}
	}

	@Override
	public double squaredAttackRange(LivingEntity target) {
		return super.squaredAttackRange(target) + 0.3D;
	}

	@Override
	protected float getJumpVelocity() {
		return super.getJumpVelocity() * 1.2F;
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0, 0.8D, 0.2);
	}

	@Override
	public float getNameTagYOffset() {
		return 1.5F;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return pose == EntityPose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(pose).scaled(this.getScaleFactor() + (this.isBaby() ? 4F : 0.0F));
	}

	@Override
	public float getScaleFactor() {
		float growthFactor = (this.dataTracker.get(GROWTH) - 5) / 30F;
		float productivityFactor = (this.dataTracker.get(PRODUCTIVITY) - 5) / 30F;
		float tierFactor = this.getTierOrDefault() / 10F;
		return (this.isBaby() ? 0.4F : 1.0F) + growthFactor + productivityFactor + tierFactor;
	}

	@Override
	protected boolean hasWings() {
		return true;
	}

	@Override
	public int getXpToDrop() {
		return super.getXpToDrop() * this.getTierOrDefault();
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	public void setBaby(boolean baby) {
		this.setBreedingAge(baby ? (900 * this.getGrowth() - 24000) : 0);
	}

	@Override
	protected void dropLoot(DamageSource source, boolean causedByPlayer) {
		super.dropLoot(source, causedByPlayer);

		if (this.getHonkType() != null) {
			int count = DROP_COUNT.get(this.getWorld().random);
			ItemStack stack = this.getHonkType().output().copy();
			stack.setCount(count);
			this.dropStack(stack);
		}
	}

	//endregion

	//region DataTracker
	@Nullable
	public HonkType getHonkType() {
		return HonkType.ENTRIES.get(this.dataTracker.get(TYPE));
	}

	public void setHonkType(HonkType type) {
		this.dataTracker.set(TYPE, Objects.requireNonNull(type.id()));
	}

	public int getTierOrDefault() {
		return this.getHonkType() == null ? 0 : this.getHonkType().tier();
	}

	public Optional<Integer> getParent() {
		return this.dataTracker.get(PARENT);
	}

	public int getFoodLevel() {
		return this.dataTracker.get(FOOD_LEVEL);
	}

	public void setFoodLevel(int foodLevel) {
		this.dataTracker.set(FOOD_LEVEL, foodLevel);
	}

	public int getGrowth() {
		return this.dataTracker.get(GROWTH);
	}

	public int getProductivity() {
		return this.dataTracker.get(PRODUCTIVITY);
	}

	public int getReproductivity() {
		return this.dataTracker.get(REPRODUCTIVITY);
	}

	public int getInstability() {
		return this.dataTracker.get(INSTABILITY);
	}
	//endregion
}
