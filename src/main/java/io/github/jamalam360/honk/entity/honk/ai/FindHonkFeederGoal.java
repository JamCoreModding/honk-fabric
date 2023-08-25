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

package io.github.jamalam360.honk.entity.honk.ai;

import io.github.jamalam360.honk.block.feeder.FeederBlockEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Optional;

public class FindHonkFeederGoal extends Goal {
	protected final HonkEntity honk;
	private final double speed;
	private Path path;
	private BlockPos target;
	private int cooldown;
	private long lastUpdateTime;

	public FindHonkFeederGoal(HonkEntity honk, double speed) {
		this.honk = honk;
		this.speed = speed;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		if (this.honk.getDataTracker().get(HonkEntity.HUNGER_DISABLED)) {
			return false;
		}

		long l = this.honk.getWorld().getTime();
		if (l - this.lastUpdateTime < 20L) {
			return false;
		} else if (this.honk.getHealth() < this.honk.getMaxHealth() * 0.4) {
			return true;
		} else if (this.honk.getFoodLevel() > 20) {
			return false;
		} else if (this.honk.getTarget() != null) {
			return false;
		} else {
			this.lastUpdateTime = l;

			Optional<BlockPos> pos = BlockPos.findClosest(this.honk.getBlockPos(), 10, 4, this::canEatFrom);

			if (pos.isEmpty()) {
				return false;
			}

			this.target = pos.get();
			this.path = this.honk.getNavigation().findPathTo(this.target, 1);

			if (this.path != null) {
				return true;
			} else {
				Vec3d posAsV3d = Vec3d.ofBottomCenter(this.target);
				return this.honk.squaredDistanceTo(posAsV3d.getX(), posAsV3d.getY(), posAsV3d.getZ()) < 1.4D;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.target == null) {
			return false;
		} else if (!this.canEatFrom(this.target)) {
			return false;
		} else if (!this.honk.isInWalkTargetRange(this.target)) {
			return false;
		} else {
			return !(this.honk.getHealth() > this.honk.getMaxHealth() * 0.75 && this.honk.getFoodLevel() > 35);
		}
	}

	@Override
	public void start() {
		this.honk.getNavigation().startMovingAlong(this.path, this.speed);
		this.cooldown = 0;
	}

	@Override
	public void stop() {
		this.target = null;
		this.honk.getNavigation().stop();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (this.target != null) {
			double targetX = this.target.getX() + 0.5;
			double targetY = this.target.getY() + 0.5;
			double targetZ = this.target.getZ() + 0.5;
			this.honk.getLookControl().lookAt(targetX, targetY, targetZ, 30.0F, 30.0F);

			if (this.cooldown != 0) {
				--this.cooldown;
			} else if (this.honk.getWorld().getBlockEntity(this.target) instanceof FeederBlockEntity entity && this.canEatFrom(this.target)) {
				ItemStack stack = entity.getStack(0).copy().withCount(1);
				entity.decrement();
				this.cooldown = 20 + this.honk.getRandom().nextInt(7);
				this.honk.eatHonkFood(stack);
			}
		}
	}

	public boolean canEatFrom(BlockPos pos) {
		if (this.honk.getWorld().getBlockEntity(pos) instanceof FeederBlockEntity entity) {
			return entity.inventory.get(0).getCount() > 0;
		} else {
			return false;
		}
	}
}
