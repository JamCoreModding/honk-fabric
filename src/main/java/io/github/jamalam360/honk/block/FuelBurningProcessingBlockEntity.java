package io.github.jamalam360.honk.block;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;

public class FuelBurningProcessingBlockEntity extends AbstractProcessingBlockEntity {

    public static final int FUEL_SLOT = 0;
    private int burnTime = 0;

    public FuelBurningProcessingBlockEntity(BlockEntityType<?> type, RecipeType<? extends Recipe<Inventory>> recipeType, int inventorySize, BlockPos pos, BlockState state) {
        super(type, recipeType, inventorySize, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.burnTime > 0) {
            this.burnTime--;

            if (this.burnTime == 0 && this.getCurrentRecipe() != null) {
                this.tryBurnItemOrCancelRecipe();
            }
        }
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    @Override
    public boolean isPowered() {
        return ItemContentRegistries.FUEL_TIME.get(this.getStack(FUEL_SLOT).getItem()).orElse(0) > 0 || this.burnTime > 0;
    }

    @Override
    public void onBeginProcessing() {
        if (this.burnTime == 0) {
            this.tryBurnItemOrCancelRecipe();
        }
    }

    public void tryBurnItemOrCancelRecipe() {
        Optional<Integer> fuelTime = ItemContentRegistries.FUEL_TIME.get(this.getStack(FUEL_SLOT).getItem());

        if (fuelTime.isPresent()) {
            this.getStack(FUEL_SLOT).decrement(1);
            this.burnTime = fuelTime.get();
        }

        if (this.burnTime == 0) {
            this.cancelCurrentRecipe();
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.burnTime = nbt.getInt("BurnTime");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("BurnTime", this.burnTime);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return null;
    }
}
