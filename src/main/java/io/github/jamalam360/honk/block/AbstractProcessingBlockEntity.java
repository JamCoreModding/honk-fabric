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

package io.github.jamalam360.honk.block;

import io.github.jamalam360.honk.api.PowerProvider;
import io.github.jamalam360.honk.api.TickingBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class AbstractProcessingBlockEntity extends BlockEntity implements PowerProvider, SidedInventory, TickingBlockEntity, NamedScreenHandlerFactory {

    public final DefaultedList<ItemStack> inventory;
    public final InventoryStorage storage;
    private final RecipeType<? extends Recipe<Inventory>> recipeType;
    public int processingTime = 0;
    private Recipe<Inventory> currentRecipe = null;

    public AbstractProcessingBlockEntity(BlockEntityType<?> type, RecipeType<? extends Recipe<Inventory>> recipeType, int inventorySize, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        this.storage = InventoryStorage.of(this, Direction.UP);
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
            if (!this.currentRecipe.matches(this, this.getWorld()) || !this.isPowered()) {
                this.cancelCurrentRecipe();
            }
        }
    }

    @Override
    public void tick() {
        if (this.currentRecipe != null) {
            if (this.processingTime == 0) {
                this.onRecipeCrafted(this.currentRecipe.craft(this, this.world.getRegistryManager()));
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

    public SoundEvent getProcessingSound() {
        return null;
    }

    public void playProcessingSound() {
        if (!this.world.isClient && this.getProcessingSound() != null) {
            this.world.playSound(
                  null,
                  this.getPos(),
                  this.getProcessingSound(),
                  SoundCategory.BLOCKS,
                  0.75f,
                  1f
            );
        }
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
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(this.inventory, slot, count);

        if (!result.isEmpty()) {
            markDirty();
        }

        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
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
