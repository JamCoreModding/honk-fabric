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

package io.github.jamalam360.honk.block.centrifuge;

import io.github.jamalam360.honk.block.AbstractProcessingScreenHandler;
import io.github.jamalam360.honk.registry.HonkScreens;
import io.github.jamalam360.honk.util.ExtractOnlySlot;
import io.wispforest.owo.client.screens.ValidatingSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;

public class CentrifugeScreenHandler extends AbstractProcessingScreenHandler {

	public CentrifugeScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(4), ScreenHandlerContext.EMPTY, new ArrayPropertyDelegate(3));
	}

	public CentrifugeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerContext context, PropertyDelegate propertyDelegate) {
		super(HonkScreens.CENTRIFUGE, syncId, playerInventory, inventory, context, propertyDelegate);
		checkSize(this.inventory, 4);
		checkDataCount(this.propertyDelegate, 3);
	}

	@Override
	public void addSlots(PlayerInventory playerInventory) {
		this.addSlot(new ValidatingSlot(this.inventory, CentrifugeBlockEntity.FUEL_SLOT, 8, 46, (stack) -> ItemContentRegistries.FUEL_TIMES.get(stack.getItem()).isPresent()));
		this.addSlot(new Slot(this.inventory, CentrifugeBlockEntity.INPUT_SLOT, 44, 34));
		this.addSlot(new ExtractOnlySlot(this.inventory, CentrifugeBlockEntity.OUTPUT_SLOT, 116, 34));
		this.addSlot(new ExtractOnlySlot(this.inventory, CentrifugeBlockEntity.REMAINDER_SLOT, 138, 34));
		super.addSlots(playerInventory);
	}
}
