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

package io.github.jamalam360.honk.compatibility.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.jamalam360.honk.block.dna_combinator.DnaCombinatorBlockEntity;
import io.github.jamalam360.honk.data.recipe.DnaCombinatorRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class DnaCombinatorEmiRecipe extends BasicEmiRecipe {

    private static final EmiTexture EMPTY_ARROW = new EmiTexture(EmiCompatibility.SPRITE_SHEET, 107, 0, 43, 27);
    private static final EmiTexture FULL_ARROW = new EmiTexture(EmiCompatibility.SPRITE_SHEET, 107, 27, 43, 28);

    public DnaCombinatorEmiRecipe(DnaCombinatorRecipe recipe) {
        super(EmiCompatibility.DNA_COMBINATOR_CATEGORY, recipe.getId(), 92, 43);

        for (Ingredient ing : recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ing));
        }

        for (EmiIngredient emiIngredient : this.inputs) {
            for (EmiStack emiStack : emiIngredient.getEmiStacks()) {
                Item item = emiStack.getItemStack().getItem();

                if (item.hasRecipeRemainder()) {
                    emiStack.setRemainder(EmiStack.of(item.getRecipeRemainder()));
                }
            }
        }

        this.outputs.add(EmiStack.of(recipe.getOutput()));
    }

    public DnaCombinatorEmiRecipe(Identifier id, ItemStack firstInput, ItemStack secondInput, ItemStack output) {
        super(EmiCompatibility.DNA_COMBINATOR_CATEGORY, id, 92, 43);

        this.inputs.add(EmiStack.of(firstInput));
        this.inputs.add(EmiStack.of(secondInput));
        this.outputs.add(EmiStack.of(output));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EMPTY_ARROW, 24, 7);
        widgets.addAnimatedTexture(FULL_ARROW, 24, 7, (DnaCombinatorBlockEntity.getDnaCombinatorProcessingTime() / 20) * 1000, true, false, false);
        widgets.addSlot(inputs.get(0), 0, 0);
        widgets.addSlot(inputs.get(1), 0, 24);
        widgets.addSlot(outputs.get(0), 73, 12).recipeContext(this);
    }
}
