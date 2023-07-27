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

import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.SlotGenerator;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

public abstract class AbstractProcessingScreenHandler extends ScreenHandler {

	public final Inventory inventory;
	public final ScreenHandlerContext context;
	public final PropertyDelegate propertyDelegate;

	public AbstractProcessingScreenHandler(ScreenHandlerType<? extends AbstractProcessingScreenHandler> type, int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerContext context, PropertyDelegate propertyDelegate) {
		super(type, syncId);
		this.inventory = inventory;
		this.inventory.onOpen(playerInventory.player);
		this.context = context;
		this.propertyDelegate = propertyDelegate;
		this.addProperties(this.propertyDelegate);
		this.addSlots(playerInventory);
	}

	public void addSlots(PlayerInventory playerInventory) {
		SlotGenerator.begin(this::addSlot, 8, 84).playerInventory(playerInventory);
	}

	@Override
	public ItemStack quickTransfer(PlayerEntity player, int fromIndex) {
		return ScreenUtils.handleSlotTransfer(this, fromIndex, this.inventory.size());
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public void sendContentUpdates() {
		this.context.run((world, pos) -> {
			BlockEntity entity = world.getBlockEntity(pos);

			if (entity instanceof AbstractProcessingBlockEntity abstractProcessingBlockEntity) {
				abstractProcessingBlockEntity.onInventoryUpdated();
			}
		});

		super.sendContentUpdates();
	}
}
