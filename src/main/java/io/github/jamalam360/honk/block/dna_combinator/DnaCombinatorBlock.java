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

package io.github.jamalam360.honk.block.dna_combinator;

import io.github.jamalam360.honk.block.FuelBurningProcessingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class DnaCombinatorBlock extends FuelBurningProcessingBlock {

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
          VoxelShapes.union(VoxelShapes.cuboid(1 / 16F, 0, 1 / 16F, 15 / 16F, 8 / 16F, 15 / 16F), VoxelShapes.cuboid(1 / 16F, 0, 1 / 16F, 7 / 16F, 12 / 16F, 15 / 16F)),
          VoxelShapes.union(VoxelShapes.cuboid(1 / 16F, 0, 1 / 16F, 15 / 16F, 8 / 16F, 15 / 16F), VoxelShapes.cuboid(9 / 16F, 0, 1 / 16F, 15 / 16F, 12 / 16F, 15 / 16F)),
          VoxelShapes.union(VoxelShapes.cuboid(1 / 16F, 0, 1 / 16F, 15 / 16F, 8 / 16F, 9 / 16F), VoxelShapes.cuboid(1 / 16F, 0, 9 / 16F, 15 / 16F, 12 / 16F, 15 / 16F)),
          VoxelShapes.union(VoxelShapes.cuboid(1 / 16F, 0, 7 / 16F, 15 / 16F, 8 / 16F, 15 / 16F), VoxelShapes.cuboid(1 / 16F, 0, 1 / 16F, 15 / 16F, 12 / 16F, 7 / 16F))};

    public DnaCombinatorBlock() {
        super(Settings.copy(Blocks.IRON_BLOCK));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DnaCombinatorBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).ordinal() - 2];
    }
}
