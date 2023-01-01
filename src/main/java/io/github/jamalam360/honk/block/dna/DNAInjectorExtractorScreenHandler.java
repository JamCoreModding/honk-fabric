package io.github.jamalam360.honk.block.dna;

import io.github.jamalam360.honk.block.AbstractProcessingScreenHandler;
import io.github.jamalam360.honk.registry.HonkScreens;
import io.github.jamalam360.honk.util.RecipeOutputSlot;
import io.wispforest.owo.client.screens.ValidatingSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;

public class DNAInjectorExtractorScreenHandler extends AbstractProcessingScreenHandler {

    public DNAInjectorExtractorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(4), ScreenHandlerContext.EMPTY, new ArrayPropertyDelegate(3));
    }

    public DNAInjectorExtractorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerContext context, PropertyDelegate propertyDelegate) {
        super(HonkScreens.DNA_INJECTOR_EXTRACTOR, syncId, playerInventory, inventory, context, propertyDelegate);

        checkSize(this.inventory, 3);
        checkDataCount(this.propertyDelegate, 3);
        this.addSlot(new ValidatingSlot(this.inventory, DNAInjectorExtractorBlockEntity.FUEL_SLOT, 8, 46, (stack) -> ItemContentRegistries.FUEL_TIME.get(stack.getItem()).isPresent()));
        this.addSlot(new Slot(this.inventory, DNAInjectorExtractorBlockEntity.INPUT_SLOT, 44, 34));
        this.addSlot(new Slot(this.inventory, DNAInjectorExtractorBlockEntity.AUXILIARY_INPUT_SLOT, 60, 34));
        this.addSlot(new RecipeOutputSlot(this.inventory, DNAInjectorExtractorBlockEntity.OUTPUT_SLOT, 116, 34));
    }
}
