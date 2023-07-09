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
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlockEntity;
import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.registry.HonkItems;
import io.github.jamalam360.honk.util.InventoryUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CentrifugeRecipe extends AutoSerializedRecipe<Inventory> {

    public static RecipeType<CentrifugeRecipe> TYPE;
    @RecipeVar
    private Ingredient input;
    @RecipeVar
    private ItemStack output;

    public static void init() {
        TYPE = AutoRecipeRegistry.registerRecipeSerializer(HonkInit.idOf("centrifuge"), CentrifugeRecipe::new);
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack input = inventory.getStack(CentrifugeBlockEntity.INPUT_SLOT);
        ItemStack remainder = input.getItem().getRecipeRemainder(input);
        return this.input.test(input) && InventoryUtils.canStack(inventory.getStack(CentrifugeBlockEntity.OUTPUT_SLOT), this.getResult(world.getRegistryManager())) && InventoryUtils.canStack(inventory.getStack(CentrifugeBlockEntity.REMAINDER_SLOT), remainder);
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager manager) {
        ItemStack input = inventory.getStack(CentrifugeBlockEntity.INPUT_SLOT);
        ItemStack inputCopy = input.copy();
        ItemStack remainder = input.getItem().getRecipeRemainder(input);
        inventory.setStack(CentrifugeBlockEntity.REMAINDER_SLOT, remainder.copy());
        input.decrement(1);

        ItemStack output = this.output.copy();

        // Special Cases
        if (inputCopy.getItem() == HonkItems.BLOOD_SYRINGE && output.getItem() == HonkItems.DNA) {
            // Initialize the DNA with the blood syringes DNA NBT
            output.setNbt(DnaData.createRandomData().writeNbt(output.getOrCreateNbt()));
        }

        return output;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager manager) {
        return this.output;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
