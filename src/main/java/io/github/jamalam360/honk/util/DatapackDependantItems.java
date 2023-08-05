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
