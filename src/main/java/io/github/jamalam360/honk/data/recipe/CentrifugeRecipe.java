package io.github.jamalam360.honk.data.recipe;

import io.github.jamalam360.autorecipe.AutoRecipeRegistry;
import io.github.jamalam360.autorecipe.AutoSerializedRecipe;
import io.github.jamalam360.autorecipe.RecipeVar;
import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlockEntity;
import io.github.jamalam360.honk.util.InventoryUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CentrifugeRecipe extends AutoSerializedRecipe<Inventory> {

    public static RecipeType<CentrifugeRecipe> TYPE;

    public static void init() {
        TYPE = AutoRecipeRegistry.registerRecipeSerializer(HonkInit.id("centrifuge"), CentrifugeRecipe::new);
    }

    @RecipeVar
    private Ingredient input;
    @RecipeVar
    private ItemStack output;

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.input.test(inventory.getStack(CentrifugeBlockEntity.INPUT_SLOT)) &&
               InventoryUtils.canStack(inventory.getStack(CentrifugeBlockEntity.OUTPUT_SLOT), this.getOutput());
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        inventory.getStack(CentrifugeBlockEntity.INPUT_SLOT).decrement(1);
        return this.output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
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
