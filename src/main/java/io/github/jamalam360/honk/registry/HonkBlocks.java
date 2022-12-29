package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlock;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlockEntity;
import io.github.jamalam360.jamlib.registry.annotation.BlockItemFactory;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;

@SuppressWarnings("unused")
@ContentRegistry(HonkInit.MOD_ID)
public class HonkBlocks {
    public static final Block CENTRIFUGE = new CentrifugeBlock();
    public static final BlockEntityType<CentrifugeBlockEntity> CENTRIFUGE_ENTITY = QuiltBlockEntityTypeBuilder.create(io.github.jamalam360.honk.block.centrifuge.CentrifugeBlockEntity::new, CENTRIFUGE).build();

    public static final Block DEEPSLATE_AMBER_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE), UniformIntProvider.create(2, 5));

    @BlockItemFactory
    public static Item createBlockItem(Block block) {
        return new BlockItem(block, new Settings().group(HonkInit.GROUP));
    }
}
