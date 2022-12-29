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

public class AbstractProcessingScreenHandler extends ScreenHandler {

    public final Inventory inventory;
    public final ScreenHandlerContext context;
    public final PropertyDelegate propertyDelegate;

    public AbstractProcessingScreenHandler(ScreenHandlerType<? extends AbstractProcessingScreenHandler> type, int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerContext context, PropertyDelegate propertyDelegate) {
        super(type, syncId);
        this.inventory = inventory;
        this.inventory.onOpen(playerInventory.player);
        this.context = context;
        this.propertyDelegate = propertyDelegate;

        SlotGenerator.begin(this::addSlot, 8, 84).playerInventory(playerInventory);
        this.addProperties(this.propertyDelegate);
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
