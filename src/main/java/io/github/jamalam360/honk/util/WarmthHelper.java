package io.github.jamalam360.honk.util;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.util.Holder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class WarmthHelper {

    public static boolean isWarm(World world, BlockPos pos) {
        Holder<Biome> biome = world.getBiome(pos);

        if (biome.value().getTemperature() >= 4) {
            return true;
        }

        return BlockPos.findClosest(pos, 10, 4, (testPos) -> (
              ((world.getBlockState(testPos).isOf(Blocks.CAMPFIRE) || world.getBlockState(testPos).isOf(Blocks.SOUL_CAMPFIRE)) && world.getBlockState(testPos).get(CampfireBlock.LIT)) ||
              ((world.getBlockState(testPos).isOf(Blocks.FURNACE) || world.getBlockState(testPos).isOf(Blocks.BLAST_FURNACE) || world.getBlockState(testPos).isOf(Blocks.SMOKER)) && world.getBlockState(testPos).get(AbstractFurnaceBlock.LIT))
        )).isPresent();
    }
}
