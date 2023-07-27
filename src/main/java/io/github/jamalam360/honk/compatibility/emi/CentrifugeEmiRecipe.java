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
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlockEntity;
import io.github.jamalam360.honk.data.recipe.CentrifugeRecipe;
import net.minecraft.item.Item;

public class CentrifugeEmiRecipe extends BasicEmiRecipe {

	private static final EmiTexture EMPTY_ARROW = new EmiTexture(EmiCompatibility.SPRITE_SHEET, 32, 0, 36, 29);
	private static final EmiTexture FULL_ARROW = new EmiTexture(EmiCompatibility.SPRITE_SHEET, 32, 29, 36, 29);

	public CentrifugeEmiRecipe(CentrifugeRecipe recipe) {
		super(EmiCompatibility.CENTRIFUGE_CATEGORY, recipe.getId(), 80, 31);
		this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(0)));

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

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EMPTY_ARROW, 22, 1);
		widgets.addAnimatedTexture(FULL_ARROW, 22, 1, (CentrifugeBlockEntity.getCentrifugeProcessingTime() / 20) * 1000, true, false, false);
		widgets.addSlot(inputs.get(0), 0, 7);
		widgets.addSlot(outputs.get(0), 62, 7).recipeContext(this);
	}
}
