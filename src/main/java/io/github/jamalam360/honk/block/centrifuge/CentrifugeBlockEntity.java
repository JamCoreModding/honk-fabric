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

package io.github.jamalam360.honk.block.centrifuge;

import io.github.jamalam360.honk.block.FuelBurningProcessingBlockEntity;
import io.github.jamalam360.honk.data.recipe.CentrifugeRecipe;
import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.registry.HonkSounds;
import io.github.jamalam360.honk.util.ReadOnlyPropertyDelegate;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;

import java.util.Arrays;
import java.util.Map;

public class CentrifugeBlockEntity extends FuelBurningProcessingBlockEntity implements SidedStorageBlockEntity {

	public static final int FUEL_SLOT = 0;
	public static final int INPUT_SLOT = 1;
	public static final int OUTPUT_SLOT = 2;
	public static final int REMAINDER_SLOT = 3;

	public static final int BURN_TIME_PROPERTY = 0;
	public static final int MAX_BURN_TIME_PROPERTY = 1;
	public static final int RECIPE_PROGRESS_PROPERTY = 2;

	public static final Map<Direction, Integer[]> SLOTS = Map.of(
			Direction.UP, new Integer[]{INPUT_SLOT},
			Direction.DOWN, new Integer[]{OUTPUT_SLOT, REMAINDER_SLOT},
			Direction.NORTH, new Integer[]{FUEL_SLOT},
			Direction.SOUTH, new Integer[]{FUEL_SLOT},
			Direction.EAST, new Integer[]{FUEL_SLOT},
			Direction.WEST, new Integer[]{FUEL_SLOT}
	);

	private final PropertyDelegate propertyDelegate = new ReadOnlyPropertyDelegate() {
		@Override
		public int get(int index) {
			return switch (index) {
				case CentrifugeBlockEntity.BURN_TIME_PROPERTY -> CentrifugeBlockEntity.this.getBurnTime();
				case CentrifugeBlockEntity.MAX_BURN_TIME_PROPERTY -> CentrifugeBlockEntity.this.getMaxBurnTime();
				case CentrifugeBlockEntity.RECIPE_PROGRESS_PROPERTY -> CentrifugeBlockEntity.this.getProcessingTime();
				default -> throw new IllegalArgumentException("Invalid property index");
			};
		}

		@Override
		public int size() {
			return 3;
		}
	};

	public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
		super(HonkBlocks.CENTRIFUGE_ENTITY, CentrifugeRecipe.TYPE, 4, FUEL_SLOT, pos, state);
	}

	public static int getCentrifugeProcessingTime() {
		return 260;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return Arrays.stream(SLOTS.get(side)).mapToInt(Integer::intValue).toArray();
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		if (dir == Direction.DOWN) {
			return false;
		} else if (dir == Direction.UP) {
			return true;
		} else {
			return ItemContentRegistries.FUEL_TIMES.get(stack.getItem()).isPresent();
		}
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new CentrifugeScreenHandler(i, playerInventory, this, ScreenHandlerContext.create(this.world, this.getPos()), propertyDelegate);
	}

	@Override
	public void onRecipeCrafted(ItemStack output) {
		if (this.getStack(OUTPUT_SLOT).isEmpty()) {
			this.inventory.set(OUTPUT_SLOT, output);
		} else {
			this.inventory.get(OUTPUT_SLOT).increment(output.getCount());
		}
	}

	@Override
	public SoundEvent getProcessingSound() {
		return HonkSounds.CENTRIFUGE;
	}

	@Override
	public int getProcessingSoundLength() {
		return (int) (1.507f * 20);
	}

	@Override
	public void onBeginProcessing() {
		if ((!this.getStack(OUTPUT_SLOT).isEmpty() && this.getStack(OUTPUT_SLOT).getItem() != this.getCurrentRecipe().getResult(this.world.getRegistryManager()).getItem()) || this.getStack(OUTPUT_SLOT).getCount() + this.getCurrentRecipe().getResult(this.world.getRegistryManager()).getCount() > this.getStack(OUTPUT_SLOT).getMaxCount()) {
			this.cancelCurrentRecipe();
			return;
		}

		super.onBeginProcessing();
		this.processingTime = getCentrifugeProcessingTime();
	}
}
