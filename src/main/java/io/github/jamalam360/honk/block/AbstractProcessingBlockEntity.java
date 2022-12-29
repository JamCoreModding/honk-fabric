package io.github.jamalam360.honk.block;

import io.github.jamalam360.honk.api.PowerProvider;
import io.github.jamalam360.honk.api.TickingBlockEntity;
import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractProcessingBlockEntity extends BlockEntity implements PowerProvider, ImplementedInventory, TickingBlockEntity, NamedScreenHandlerFactory {

    public final DefaultedList<ItemStack> inventory;
    private final RecipeType<? extends Recipe<Inventory>> recipeType;
    private Recipe<Inventory> currentRecipe = null;
    public int processingTime = 0;

    public AbstractProcessingBlockEntity(BlockEntityType<?> type, RecipeType<? extends Recipe<Inventory>> recipeType, int inventorySize, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        this.recipeType = recipeType;
    }

    public void onInventoryUpdated() {
        if (this.currentRecipe == null) {
            if (this.isPowered()) {
                this.getWorld().getRecipeManager().getFirstMatch(this.recipeType, this, this.getWorld()).ifPresent((recipe) -> {
                    this.currentRecipe = recipe;
                    this.onBeginProcessing();
                });
            }
        } else {
            if(!this.currentRecipe.matches(this, this.getWorld()) || !this.isPowered()) {
                this.cancelCurrentRecipe();
            }
        }
    }

    @Override
    public void tick() {
        if (this.currentRecipe != null) {
            if (this.processingTime == 0) {
                this.onRecipeCrafted(this.currentRecipe.craft(this));
                this.currentRecipe = null;
            } else if (this.processingTime > 0) {
                this.processingTime--;
            }
        }
    }

    public void onRecipeCrafted(ItemStack output) {
    }

    public Recipe<Inventory> getCurrentRecipe() {
        return this.currentRecipe;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public void cancelCurrentRecipe() {
        this.processingTime = 0;
        this.currentRecipe = null;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(this.getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
    }
}
