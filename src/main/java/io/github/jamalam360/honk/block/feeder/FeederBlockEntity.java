package io.github.jamalam360.honk.block.feeder;

import io.github.jamalam360.honk.block.AbstractBlockEntityWithInventory;
import io.github.jamalam360.honk.registry.HonkBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class FeederBlockEntity extends AbstractBlockEntityWithInventory {
	public FeederBlockEntity(BlockPos pos, BlockState state) {
		super(HonkBlocks.FEEDER_ENTITY, 1, pos, state);
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return new int[0];
		} else {
			return new int[]{0};
		}
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return stack.isFood();
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}
}
