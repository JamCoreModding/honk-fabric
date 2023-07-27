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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractProcessingBlockEntity extends AbstractBlockEntityWithInventory implements PowerProvider, TickingBlockEntity, NamedScreenHandlerFactory {

	private final RecipeType<? extends Recipe<Inventory>> recipeType;
	public int processingTime = 0;
	private int processingSoundTicks = 0;
	private Recipe<Inventory> currentRecipe = null;

	public AbstractProcessingBlockEntity(BlockEntityType<?> type, RecipeType<? extends Recipe<Inventory>> recipeType, int inventorySize, BlockPos pos, BlockState state) {
		super(type, inventorySize, pos, state);
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
				this.processingSoundTicks = 0;
			} else if (this.processingTime > 0) {
				this.processingTime--;
				this.processingSoundTicks--;

				if (this.processingSoundTicks <= 0) {
					this.playProcessingSound();
					this.processingSoundTicks = this.getProcessingSoundLength();
				}
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

	public int getProcessingSoundLength() {
		return 0;
	}

	public void playProcessingSound() {
		if (!this.world.isClient && this.getProcessingSound() != null && this.getProcessingSoundLength() != 0) {
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
		this.markDirty();
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable(this.getCachedState().getBlock().getTranslationKey());
	}
}
