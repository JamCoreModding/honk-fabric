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

package io.github.jamalam360.honk.data;


import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information in DNA in NBT (such as the DNA item, blood syringe, etc.) - genetics (growth, productivity, reproductivity, instability) - type
 */
public record DnaData(HonkType type, int growth, int productivity, int reproductivity, int instability) {

	public static DnaData createRandomData() {
		return new DnaData(
				HonkType.getRandom(1),
				//TODO: some randomness here i reckon
				1,
				1,
				1,
				1
		);
	}

	public static DnaData fromNbt(NbtCompound compound) {
		NbtCompound tag = compound.getCompound(NbtKeys.DNA);

		return new DnaData(
				HonkType.ENTRIES.get(tag.getString(NbtKeys.TYPE)),
				tag.getInt(NbtKeys.GROWTH),
				tag.getInt(NbtKeys.PRODUCTIVITY),
				tag.getInt(NbtKeys.REPRODUCTIVITY),
				tag.getInt(NbtKeys.INSTABILITY)
		);
	}

	public static DnaData fromEntity(HonkEntity entity) {
		return new DnaData(
				HonkType.ENTRIES.get(entity.getDataTracker().get(HonkEntity.TYPE)),
				entity.getGrowth(),
				entity.getProductivity(),
				entity.getReproductivity(),
				entity.getInstability()
		);
	}

	public NbtCompound writeNbt(NbtCompound compound) {
		NbtCompound tag = new NbtCompound();

		tag.putString(NbtKeys.TYPE, this.type.id());
		tag.putInt(NbtKeys.GROWTH, this.growth);
		tag.putInt(NbtKeys.PRODUCTIVITY, this.productivity);
		tag.putInt(NbtKeys.REPRODUCTIVITY, this.reproductivity);
		tag.putInt(NbtKeys.INSTABILITY, this.instability);

		compound.put(NbtKeys.DNA, tag);
		return compound;
	}

	public void writeEntity(HonkEntity entity) {
		entity.getDataTracker().set(HonkEntity.TYPE, this.type.id());
		entity.getDataTracker().set(HonkEntity.GROWTH, this.growth);
		entity.getDataTracker().set(HonkEntity.PRODUCTIVITY, this.productivity);
		entity.getDataTracker().set(HonkEntity.REPRODUCTIVITY, this.reproductivity);
		entity.getDataTracker().set(HonkEntity.INSTABILITY, this.instability);
	}

	public void writeEntity(EggEntity entity) {
		entity.getDataTracker().set(EggEntity.TYPE, this.type.id());
		entity.getDataTracker().set(EggEntity.GROWTH, this.growth);
		entity.getDataTracker().set(EggEntity.PRODUCTIVITY, this.productivity);
		entity.getDataTracker().set(EggEntity.REPRODUCTIVITY, this.reproductivity);
		entity.getDataTracker().set(EggEntity.INSTABILITY, this.instability);
	}

	public List<Text> getInformation() {
		List<Text> result = new ArrayList<>();
		result.add(Text.translatable("text.honk.info_dna", this.type().name()).styled(s -> s.withBold(true)));
		result.add(Text.translatable("text.honk.info_type", Text.translatable(this.type().output().getTranslationKey()), Integer.toString(this.type().tier())).styled(s -> s.withItalic(true).withColor(Formatting.AQUA)));
		result.add(Text.literal(""));
		result.add(Text.translatable("text.honk.info_growth", this.growth()).styled(s -> s.withColor(Formatting.GRAY)));
		result.add(Text.translatable("text.honk.info_productivity", this.productivity()).styled(s -> s.withColor(Formatting.GRAY)));
		result.add(Text.translatable("text.honk.info_reproductivity", this.reproductivity()).styled(s -> s.withColor(Formatting.GRAY)));
		result.add(Text.translatable("text.honk.info_instability", this.instability()).styled(s -> s.withColor(Formatting.GRAY)));
		return result;
	}
}
