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
