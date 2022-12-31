package io.github.jamalam360.honk.block.centrifuge;

import io.github.jamalam360.honk.block.FuelBurningProcessingBlockEntity;
import io.github.jamalam360.honk.data.recipes.CentrifugeRecipe;
import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.util.ReadOnlyPropertyDelegate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;

public class CentrifugeBlockEntity extends FuelBurningProcessingBlockEntity {

    public static final int FUEL_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    public static final int BURN_TIME_PROPERTY = 0;
    public static final int MAX_BURN_TIME_PROPERTY = 1;
    public static final int RECIPE_PROGRESS_PROPERTY = 2;

    private final PropertyDelegate propertyDelegate = new ReadOnlyPropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case CentrifugeBlockEntity.BURN_TIME_PROPERTY -> CentrifugeBlockEntity.this.getBurnTime();
                case CentrifugeBlockEntity.MAX_BURN_TIME_PROPERTY -> ItemContentRegistries.FUEL_TIME.get(CentrifugeBlockEntity.this.getStack(FUEL_SLOT).getItem()).orElse(0);
                case CentrifugeBlockEntity.RECIPE_PROGRESS_PROPERTY -> CentrifugeBlockEntity.this.getProcessingTime();
                default -> throw new IllegalArgumentException("Invalid property index");
            };
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(HonkBlocks.CENTRIFUGE_ENTITY, CentrifugeRecipe.TYPE, 3, FUEL_SLOT, pos, state);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new CentrifugeScreenHandler(i, playerInventory, this, ScreenHandlerContext.create(this.world, this.getPos()), propertyDelegate);
    }

    @Override
    public void onRecipeCrafted(ItemStack output) {
        if (this.getStack(OUTPUT_SLOT).isEmpty()) {
            this.inventory.set(OUTPUT_SLOT, output);
        } else {
            this.inventory.get(OUTPUT_SLOT).increment(output.getCount());
        }
    }

    @Override
    public void onBeginProcessing() {
        if ((!this.getStack(OUTPUT_SLOT).isEmpty() && this.getStack(OUTPUT_SLOT).getItem() != this.getCurrentRecipe().getOutput().getItem()) || this.getStack(OUTPUT_SLOT).getCount() + this.getCurrentRecipe().getOutput().getCount() > this.getStack(OUTPUT_SLOT).getMaxCount()) {
            this.cancelCurrentRecipe();
            return;
        }

        super.onBeginProcessing();
        this.processingTime = getCentrifugeProcessingTime();
    }

    public static int getCentrifugeProcessingTime() {
        return 100;
    }
}
