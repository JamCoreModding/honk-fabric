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

package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.ModLogoBlock;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlock;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlockEntity;
import io.github.jamalam360.honk.block.dna_combinator.DnaCombinatorBlock;
import io.github.jamalam360.honk.block.dna_combinator.DnaCombinatorBlockEntity;
import io.github.jamalam360.honk.block.dna_injector_extractor.DnaInjectorExtractorBlock;
import io.github.jamalam360.honk.block.dna_injector_extractor.DnaInjectorExtractorBlockEntity;
import io.github.jamalam360.honk.block.feeder.FeederBlock;
import io.github.jamalam360.honk.block.feeder.FeederBlockEntity;
import io.github.jamalam360.jamlib.registry.JamLibContentRegistry;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.int_provider.UniformIntProvider;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

@SuppressWarnings("unused")
@ContentRegistry(HonkInit.MOD_ID)
public class HonkBlocks implements JamLibContentRegistry {

	public static final Block MOD_LOGO = new ModLogoBlock();
	public static final Block DEEPSLATE_AMBER_ORE = new ExperienceDroppingBlock(QuiltBlockSettings.copy(Blocks.DEEPSLATE_IRON_ORE), UniformIntProvider.create(2, 5));
	public static final Block CENTRIFUGE = new CentrifugeBlock();
	public static final Block DNA_INJECTOR_EXTRACTOR = new DnaInjectorExtractorBlock();
	public static final Block DNA_COMBINATOR = new DnaCombinatorBlock();
	public static final Block FEEDER = new FeederBlock();

	@Override
	public RegistryKey<ItemGroup> getItemGroup(Item item) {
		return HonkInit.MAIN_GROUP_KEY;
	}

	public static final BlockEntityType<CentrifugeBlockEntity> CENTRIFUGE_ENTITY = QuiltBlockEntityTypeBuilder.create(CentrifugeBlockEntity::new, CENTRIFUGE).build();


	public static final BlockEntityType<DnaInjectorExtractorBlockEntity> DNA_INJECTOR_EXTRACTOR_ENTITY = QuiltBlockEntityTypeBuilder.create(DnaInjectorExtractorBlockEntity::new, DNA_INJECTOR_EXTRACTOR).build();


	public static final BlockEntityType<DnaCombinatorBlockEntity> DNA_COMBINATOR_ENTITY = QuiltBlockEntityTypeBuilder.create(DnaCombinatorBlockEntity::new, DNA_COMBINATOR).build();

	public static final BlockEntityType<FeederBlockEntity> FEEDER_ENTITY = QuiltBlockEntityTypeBuilder.create(FeederBlockEntity::new, FEEDER).build();


}
