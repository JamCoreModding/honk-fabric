package io.github.jamalam360.honk.block;

import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

public abstract class FuelBurningProcessingBlock extends AbstractProcessingBlock {

    public FuelBurningProcessingBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
        if (world.getBlockEntity(pos) instanceof FuelBurningProcessingBlockEntity entity && entity.getBurnTime() > 0) {
            double d = (double) pos.getX() + 0.5;
            double e = pos.getY();
            double f = (double) pos.getZ() + 0.5;

            if (random.nextDouble() < 0.1) {
                world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.get(FACING);
            Direction.Axis axis = direction.getAxis();
            double h = random.nextDouble() * 0.6 - 0.3;
            double i = axis == Direction.Axis.X ? (double) direction.getOffsetX() * 0.52 : h;
            double j = random.nextDouble() * 9.0 / 16.0;
            double k = axis == Direction.Axis.Z ? (double) direction.getOffsetZ() * 0.52 : h;
            world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
        }
    }
}
