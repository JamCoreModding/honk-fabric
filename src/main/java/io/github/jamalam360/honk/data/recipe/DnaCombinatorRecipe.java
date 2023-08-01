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
import io.github.jamalam360.honk.block.dna_combinator.DnaCombinatorBlockEntity;
import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.util.InventoryUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DnaCombinatorRecipe extends AutoSerializedRecipe<Inventory> {

	public static RecipeType<DnaCombinatorRecipe> TYPE;
	@RecipeVar
	public Ingredient firstInput;
	@RecipeVar
	public Ingredient secondInput;
	@RecipeVar
	public ItemStack output;

	public static void init() {
		TYPE = AutoRecipeRegistry.registerRecipeSerializer(HonkInit.idOf("dna_combinator"), DnaCombinatorRecipe::new);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		ItemStack firstInput = inventory.getStack(DnaCombinatorBlockEntity.FIRST_INPUT_SLOT);
		ItemStack secondInput = inventory.getStack(DnaCombinatorBlockEntity.SECOND_INPUT_SLOT);

		if (firstInput.getItem() == HonkItems.DNA && secondInput.getItem() == HonkItems.DNA) {
			HonkType result = this.getResultFromDnaCombining(inventory);

			return result != null;
		}

		boolean match1 = this.firstInput.test(firstInput) && this.secondInput.test(secondInput);
		boolean match2 = this.firstInput.test(secondInput) && this.secondInput.test(firstInput);

		return (match1 || match2) && InventoryUtils.canStack(inventory.getStack(DnaCombinatorBlockEntity.OUTPUT_SLOT), this.getResult(world.getRegistryManager()));
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager manager) {
		ItemStack firstInputCopy = inventory.getStack(DnaCombinatorBlockEntity.FIRST_INPUT_SLOT).copy();
		ItemStack secondInputCopy = inventory.getStack(DnaCombinatorBlockEntity.SECOND_INPUT_SLOT).copy();
		inventory.getStack(DnaCombinatorBlockEntity.FIRST_INPUT_SLOT).decrement(1);
		inventory.getStack(DnaCombinatorBlockEntity.SECOND_INPUT_SLOT).decrement(1);
		ItemStack output = this.output.copy();

		if (firstInputCopy.getItem() == HonkItems.DNA && secondInputCopy.getItem() == HonkItems.DNA) {
			HonkType result = this.getResultFromDnaCombining(inventory);

			if (result == null) {
				result = HonkType.getRandom(1);
				HonkInit.LOGGER.warn("Failed to combine DNA, defaulting to random DNA");
			}

			new DnaData(result, 1, 1, 1, 1).writeNbt(output.getOrCreateNbt());
		}

		return output;
	}

	@Nullable
	private HonkType getResultFromDnaCombining(Inventory inventory) {
		ItemStack firstInput = inventory.getStack(DnaCombinatorBlockEntity.FIRST_INPUT_SLOT);
		ItemStack secondInput = inventory.getStack(DnaCombinatorBlockEntity.SECOND_INPUT_SLOT);

		if (firstInput.getItem() == HonkItems.DNA && secondInput.getItem() == HonkItems.DNA) {
			DnaData firstDna = DnaData.fromNbt(firstInput.getOrCreateNbt());
			DnaData secondDna = DnaData.fromNbt(secondInput.getOrCreateNbt());
			HonkType result = null;

			if (firstDna.type() == secondDna.type()) {
				result = firstDna.type();
			} else {
				Identifier first = new Identifier(firstDna.type().id());
				Identifier second = new Identifier(secondDna.type().id());

				for (HonkType potentialResult : HonkType.ENTRIES.values()) {
					for (List<Identifier> parents : potentialResult.parents()) {
						if ((parents.get(0).equals(first) && parents.get(1).equals(second)) || (parents.get(0).equals(second) && parents.get(1).equals(first))) {
							result = potentialResult;
							break;
						}
					}
				}
			}

			return result;
		} else {
			return null;
		}
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(this.firstInput);
		defaultedList.add(this.secondInput);
		return defaultedList;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager manager) {
		return this.output;
	}

	public Ingredient getFirstInput() {
		return this.firstInput;
	}

	public Ingredient getSecondInput() {
		return this.secondInput;
	}

	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}
}
