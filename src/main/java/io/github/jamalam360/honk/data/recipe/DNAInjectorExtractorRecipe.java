package io.github.jamalam360.honk.data.recipe;

import io.github.jamalam360.autorecipe.AutoRecipeRegistry;
import io.github.jamalam360.autorecipe.AutoSerializedRecipe;
import io.github.jamalam360.autorecipe.RecipeVar;
import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.dna.DNAInjectorExtractorBlockEntity;
import io.github.jamalam360.honk.util.InventoryUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class DNAInjectorExtractorRecipe extends AutoSerializedRecipe<Inventory> {

    public static RecipeType<DNAInjectorExtractorRecipe> TYPE;

    public static void init() {
        TYPE = AutoRecipeRegistry.registerRecipeSerializer(HonkInit.id("dna_injector_extractor"), DNAInjectorExtractorRecipe::new);
    }

    @RecipeVar
    private Ingredient input;
    @RecipeVar(required = false)
    private Ingredient auxiliaryInput;
    @RecipeVar
    private ItemStack output;


    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.input.test(inventory.getStack(DNAInjectorExtractorBlockEntity.INPUT_SLOT))
               && (this.auxiliaryInput == null || this.auxiliaryInput.test(inventory.getStack(DNAInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT)))
               && InventoryUtils.canStack(inventory.getStack(DNAInjectorExtractorBlockEntity.OUTPUT_SLOT), this.getOutput());
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        inventory.getStack(DNAInjectorExtractorBlockEntity.INPUT_SLOT).decrement(1);

        if (this.auxiliaryInput != null) {
            inventory.getStack(DNAInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT).decrement(1);
        }

        return this.output.copy();
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
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
