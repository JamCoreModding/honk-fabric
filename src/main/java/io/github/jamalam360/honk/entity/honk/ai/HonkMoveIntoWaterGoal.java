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

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class HonkMoveIntoWaterGoal extends Goal {
	private static final int D = 3;
	private final PathAwareEntity mob;

	public HonkMoveIntoWaterGoal(PathAwareEntity mob) {
		this.mob = mob;
	}

	@Override
	public boolean canStart() {
		return this.mob.isOnGround() && !this.mob.getWorld().getFluidState(this.mob.getBlockPos()).isIn(FluidTags.WATER);
	}

	@Override
	public void start() {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.iterate(
				MathHelper.floor(this.mob.getX() - D),
				MathHelper.floor(this.mob.getY() - D),
				MathHelper.floor(this.mob.getZ() - D),
				MathHelper.floor(this.mob.getX() + D),
				MathHelper.floor(this.mob.getY() - D / 2.0),
				MathHelper.floor(this.mob.getZ() + D)
		)) {
			if (this.mob.getWorld().getFluidState(blockPos2).isIn(FluidTags.WATER)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos != null) {
			this.mob.getMoveControl().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
		}
	}
}
