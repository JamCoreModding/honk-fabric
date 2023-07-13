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
import java.util.List;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DnaCombinatorRecipe extends AutoSerializedRecipe<Inventory> {

    public static RecipeType<DnaCombinatorRecipe> TYPE;
    @RecipeVar
    private Ingredient firstInput;
    @RecipeVar
    private Ingredient secondInput;
    @RecipeVar
    private ItemStack output;

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

        return this.firstInput.test(inventory.getStack(DnaCombinatorBlockEntity.FIRST_INPUT_SLOT)) && this.secondInput.test(inventory.getStack(DnaCombinatorBlockEntity.SECOND_INPUT_SLOT)) && InventoryUtils.canStack(inventory.getStack(DnaCombinatorBlockEntity.OUTPUT_SLOT), this.getResult(world.getRegistryManager()));
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
