package io.github.jamalam360.honk.block.dna;

import io.github.jamalam360.honk.block.AbstractProcessingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class DNAInjectorExtractorBlock extends AbstractProcessingBlock {

    public DNAInjectorExtractorBlock() {
        super(Settings.copy(Blocks.IRON_BLOCK));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DNAInjectorExtractorBlockEntity(pos, state);
    }
}
