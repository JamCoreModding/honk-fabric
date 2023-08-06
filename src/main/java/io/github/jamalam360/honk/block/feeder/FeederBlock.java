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

package io.github.jamalam360.honk.block.feeder;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

@SuppressWarnings("deprecation")
public class FeederBlock extends BlockWithEntity {
	public static final Property<Integer> LEVEL = IntProperty.of("level", 0, 5);
	private static final VoxelShape RAYCAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0);
	private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE = Util.make(
			new VoxelShape[6],
			shapes -> {
				int[] levels = new int[]{2, 3, 4, 6, 8, 10};

				for (int i = 0; i < 6; i++) {
					shapes[i] = VoxelShapes.combineAndSimplify(
							RAYCAST_SHAPE, Block.createCuboidShape(1.0, levels[i], 1.0, 15.0, 10.0, 15.0), BooleanBiFunction.ONLY_FIRST
					);
				}
			}
	);

	public FeederBlock() {
		super(QuiltBlockSettings.copy(Blocks.COMPOSTER));
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 0));
	}

	public static void updateState(BlockState state, World world, BlockPos pos) {
		FeederBlockEntity feeder = (FeederBlockEntity) world.getBlockEntity(pos);
		double proportion = (double) feeder.getStack(0).getCount() / (double) feeder.getStack(0).getMaxCount();
		int level = (int) Math.ceil(proportion * 5);
		world.setBlockState(pos, state.with(LEVEL, level));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LEVEL);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!player.getAbilities().allowModifyWorld) {
			return ActionResult.PASS;
		}

		if (!world.isClient) {
			FeederBlockEntity feeder = (FeederBlockEntity) world.getBlockEntity(pos);

			if (feeder == null) {
				return ActionResult.PASS;
			}

			if (player.isSneaking() && player.getStackInHand(hand).isEmpty()) {
				if (feeder.getStack(0).getCount() > 0) {
					player.setStackInHand(hand, feeder.getStack(0));
					feeder.setStack(0, ItemStack.EMPTY);
					world.setBlockState(pos, state.with(LEVEL, 0));
					return ActionResult.SUCCESS;
				}
			} else if (player.getStackInHand(hand).isFood()) {
				ItemStack feederStack = feeder.getStack(0);
				ItemStack resultStack;

				if (feederStack.isEmpty()) {
					resultStack = player.getStackInHand(hand).copy();
					resultStack.setCount(1);

					if (!player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
				} else if (feederStack.getItem() == player.getStackInHand(hand).getItem()) {
					if (feederStack.getCount() + 1 <= feederStack.getMaxCount()) {
						resultStack = feederStack.copy();
						resultStack.increment(1);

						if (!player.isCreative()) {
							player.getStackInHand(hand).decrement(1);
						}
					} else {
						return ActionResult.PASS;
					}
				} else {
					return ActionResult.PASS;
				}

				feeder.setStack(0, resultStack);
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (world.getBlockEntity(pos) instanceof FeederBlockEntity entity) {
				if (world instanceof ServerWorld) {
					ItemScatterer.spawn(world, pos, entity);
				}

				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return LEVEL_TO_COLLISION_SHAPE[state.get(LEVEL)];
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return RAYCAST_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return LEVEL_TO_COLLISION_SHAPE[0];
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new FeederBlockEntity(pos, state);
	}
}
