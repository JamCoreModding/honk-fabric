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

package io.github.jamalam360.honk.data.recipe;

import io.github.jamalam360.autorecipe.AutoRecipeRegistry;
import io.github.jamalam360.autorecipe.AutoSerializedRecipe;
import io.github.jamalam360.autorecipe.RecipeVar;
import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.dna_injector_extractor.DnaInjectorExtractorBlockEntity;
import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.data.NbtKeys;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.util.InventoryUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Random;

public class DnaInjectorExtractorRecipe extends AutoSerializedRecipe<Inventory> {

	private static final Random RANDOM = new Random();
	public static RecipeType<DnaInjectorExtractorRecipe> TYPE;
	@RecipeVar
	private float successChance;
	@RecipeVar
	private Ingredient input;
	@RecipeVar(required = false)
	private Ingredient auxiliaryInput;
	@RecipeVar
	private ItemStack output;

	public static void init() {
		TYPE = AutoRecipeRegistry.registerRecipeSerializer(HonkInit.idOf("dna_injector_extractor"), DnaInjectorExtractorRecipe::new);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		boolean auxCondition = true;
		if (this.auxiliaryInput == null && !inventory.getStack(DnaInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT).isEmpty()) {
			return false;
		} else if (this.auxiliaryInput != null && !this.auxiliaryInput.test(inventory.getStack(DnaInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT))) {
			return false;
		} else if (this.auxiliaryInput != null) {
			ItemStack auxInput = inventory.getStack(DnaInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT);
			auxCondition = InventoryUtils.canStack(inventory.getStack(DnaInjectorExtractorBlockEntity.REMAINDER_SLOT), auxInput.getItem().getRecipeRemainder(auxInput));
		}

		return this.input.test(inventory.getStack(DnaInjectorExtractorBlockEntity.INPUT_SLOT))
				&& InventoryUtils.canStack(inventory.getStack(DnaInjectorExtractorBlockEntity.OUTPUT_SLOT), this.getResult(world.getRegistryManager()))
				&& auxCondition;
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager manager) {
		ItemStack inputCopy = inventory.getStack(DnaInjectorExtractorBlockEntity.INPUT_SLOT).copy();
		ItemStack auxiliaryInputCopy = inventory.getStack(DnaInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT).copy();
		inventory.getStack(DnaInjectorExtractorBlockEntity.INPUT_SLOT).decrement(1);

		if (this.auxiliaryInput != null) {
			ItemStack auxiliaryInput = inventory.getStack(DnaInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT);
			inventory.setStack(DnaInjectorExtractorBlockEntity.REMAINDER_SLOT, auxiliaryInput.getItem().getRecipeRemainder(auxiliaryInput).copy());
			auxiliaryInput.decrement(1);
		}

		if (RANDOM.nextFloat() <= this.successChance) {
			ItemStack output = this.output.copy();

			// Special Cases
			if (inputCopy.getItem() == HonkItems.AMBER && auxiliaryInputCopy.getItem() == HonkItems.EMPTY_SYRINGE && output.getItem() == HonkItems.BLOOD_SYRINGE) {
				// Initialize the blood syringe with random base tier data
				output.setNbt(DnaData.createRandomData().writeNbt(output.getOrCreateNbt()));
			} else if (inputCopy.getItem() == Items.EGG && auxiliaryInputCopy.getItem() == HonkItems.DNA && output.getItem() == HonkItems.EGG) {
				// Copy DNA data from auxiliary input to output
				if (auxiliaryInputCopy.getOrCreateNbt().contains(NbtKeys.DNA)) {
					output.setNbt(DnaData.fromNbt(auxiliaryInputCopy.getOrCreateNbt()).writeNbt(output.getOrCreateNbt()));
				} else {
					output.setNbt(DnaData.createRandomData().writeNbt(output.getOrCreateNbt()));
				}
			}

			return output;
		} else {
			return ItemStack.EMPTY.copy();
		}
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(this.input);

		if (this.auxiliaryInput != null) {
			defaultedList.add(this.auxiliaryInput);
		}

		return defaultedList;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager manager) {
		return this.output;
	}

	public float getSuccessChance() {
		return this.successChance;
	}

	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}
}
