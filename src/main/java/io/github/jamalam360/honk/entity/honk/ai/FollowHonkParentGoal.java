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

import io.github.jamalam360.honk.entity.honk.HonkEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FollowHonkParentGoal extends Goal {

	private final HonkEntity honk;
	private final double speed;
	@Nullable
	private HonkEntity parent;
	private int delay;

	public FollowHonkParentGoal(HonkEntity honk, double speed) {
		this.honk = honk;
		this.speed = speed;
	}

	@Override
	public boolean canStart() {
		if (this.honk.getParent().isPresent() && this.honk.getWorld().getEntityById(this.honk.getParent().get()) != null && this.honk.isBaby()) {
			Entity parent = this.honk.getWorld().getEntityById(this.honk.getParent().get());

			if (parent instanceof HonkEntity parentHonk && !parentHonk.isBaby()) {
				this.parent = parentHonk;
				return true;
			} else {
				this.honk.getDataTracker().set(HonkEntity.PARENT, Optional.empty());
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.honk.isBaby()) {
			return false;
		} else {
			double d = this.honk.squaredDistanceTo(this.parent);
			return !(d < 9.0) && !(d > 256.0);
		}
	}

	@Override
	public void start() {
		this.delay = 0;
	}

	@Override
	public void stop() {
		this.parent = null;
	}

	@Override
	public void tick() {
		if (--this.delay <= 0) {
			this.delay = this.getTickCount(10);
			this.honk.getNavigation().startMovingTo(this.parent, this.speed);
		}
	}
}
