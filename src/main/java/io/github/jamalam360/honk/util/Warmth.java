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

package io.github.jamalam360.honk.util;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.registry.Holder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class Warmth {

    public static boolean isWarm(World world, BlockPos pos) {
        Holder<Biome> biome = world.getBiome(pos);

        if (biome.value().getTemperature() >= 4) {
            return true;
        }

        return BlockPos.findClosest(pos, 8, 4, (testPos) -> (
              ((world.getBlockState(testPos).isOf(Blocks.CAMPFIRE) || world.getBlockState(testPos).isOf(Blocks.SOUL_CAMPFIRE)) && world.getBlockState(testPos).get(CampfireBlock.LIT)) ||
              ((world.getBlockState(testPos).isOf(Blocks.FURNACE) || world.getBlockState(testPos).isOf(Blocks.BLAST_FURNACE) || world.getBlockState(testPos).isOf(Blocks.SMOKER)) && world.getBlockState(testPos).get(AbstractFurnaceBlock.LIT))
        )).isPresent();
    }
}
