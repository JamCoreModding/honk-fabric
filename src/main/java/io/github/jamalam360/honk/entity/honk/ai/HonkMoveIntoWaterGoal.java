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
