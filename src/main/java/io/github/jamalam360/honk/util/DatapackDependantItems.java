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

import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.registry.HonkItems;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatapackDependantItems {
	public static List<ItemStack> createDnaGroupStacks() {
		List<ItemStack> syringes = new ArrayList<>();
		List<ItemStack> dnas = new ArrayList<>();
		List<ItemStack> eggs = new ArrayList<>();

		for (HonkType type : HonkType.ENTRIES.values().stream().sorted(Comparator.comparingInt(HonkType::tier)).toList()) {
			DnaData data = new DnaData(type, 1, 1, 1, 1);
			ItemStack syringe = HonkItems.BLOOD_SYRINGE.getDefaultStack();
			ItemStack dna = HonkItems.DNA.getDefaultStack();
			ItemStack egg = HonkItems.EGG.getDefaultStack();
			data.writeNbt(syringe.getOrCreateNbt());
			data.writeNbt(dna.getOrCreateNbt());
			data.writeNbt(egg.getOrCreateNbt());
			syringes.add(syringe);
			dnas.add(dna);
			eggs.add(egg);
		}

		// order by syringe --> dna --> egg
		List<ItemStack> result = new ArrayList<>();
		result.addAll(syringes);
		result.addAll(dnas);
		result.addAll(eggs);
		return result;
	}
}
