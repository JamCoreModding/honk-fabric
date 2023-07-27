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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class TemptHonkGoal extends Goal {
	private static final TargetPredicate TEMPTING_ENTITY_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0).ignoreVisibility();
	protected final PathAwareEntity mob;
	private final TargetPredicate predicate;
	private final double speed;
	private final boolean canBeScared;
	@Nullable
	protected PlayerEntity closestPlayer;
	private double lastPlayerX;
	private double lastPlayerY;
	private double lastPlayerZ;
	private double lastPlayerPitch;
	private double lastPlayerYaw;
	private int cooldown;
	private boolean active;

	public TemptHonkGoal(PathAwareEntity entity, double speed, boolean canBeScared) {
		this.mob = entity;
		this.speed = speed;
		this.canBeScared = canBeScared;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		this.predicate = TEMPTING_ENTITY_PREDICATE.copy().setPredicate(this::isTemptedBy);
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0) {
			--this.cooldown;
			return false;
		} else {
			this.closestPlayer = this.mob.getWorld().getClosestPlayer(this.predicate, this.mob);
			return this.closestPlayer != null;
		}
	}

	private boolean isTemptedBy(LivingEntity entity) {
		return entity.getMainHandStack().isFood() || entity.getOffHandStack().isFood();
	}

	@Override
	public boolean shouldContinue() {
		if (this.canBeScared()) {
			if (this.mob.squaredDistanceTo(this.closestPlayer) < 36.0) {
				if (this.closestPlayer.squaredDistanceTo(this.lastPlayerX, this.lastPlayerY, this.lastPlayerZ) > 0.010000000000000002) {
					return false;
				}

				if (Math.abs((double) this.closestPlayer.getPitch() - this.lastPlayerPitch) > 5.0
						|| Math.abs((double) this.closestPlayer.getYaw() - this.lastPlayerYaw) > 5.0) {
					return false;
				}
			} else {
				this.lastPlayerX = this.closestPlayer.getX();
				this.lastPlayerY = this.closestPlayer.getY();
				this.lastPlayerZ = this.closestPlayer.getZ();
			}

			this.lastPlayerPitch = this.closestPlayer.getPitch();
			this.lastPlayerYaw = this.closestPlayer.getYaw();
		}

		return this.canStart();
	}

	protected boolean canBeScared() {
		return this.canBeScared;
	}

	@Override
	public void start() {
		this.lastPlayerX = this.closestPlayer.getX();
		this.lastPlayerY = this.closestPlayer.getY();
		this.lastPlayerZ = this.closestPlayer.getZ();
		this.active = true;
	}

	@Override
	public void stop() {
		this.closestPlayer = null;
		this.mob.getNavigation().stop();
		this.cooldown = toGoalTicks(100);
		this.active = false;
	}

	@Override
	public void tick() {
		this.mob.getLookControl().lookAt(this.closestPlayer, (float) (this.mob.getBodyYawSpeed() + 20), (float) this.mob.getLookPitchSpeed());
		if (this.mob.squaredDistanceTo(this.closestPlayer) < 6.25) {
			this.mob.getNavigation().stop();
		} else {
			this.mob.getNavigation().startMovingTo(this.closestPlayer, this.speed);
		}
	}

	public boolean isActive() {
		return this.active;
	}
}
