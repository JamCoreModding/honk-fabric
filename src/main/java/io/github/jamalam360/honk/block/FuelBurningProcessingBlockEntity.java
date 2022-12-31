package io.github.jamalam360.honk.block;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;

public abstract class FuelBurningProcessingBlockEntity extends AbstractProcessingBlockEntity {

    private int fuelSlot = 0;
    private int burnTime = 0;

    public FuelBurningProcessingBlockEntity(BlockEntityType<?> type, RecipeType<? extends Recipe<Inventory>> recipeType, int inventorySize, int fuelSlot, BlockPos pos, BlockState state) {
        super(type, recipeType, inventorySize, pos, state);
        this.fuelSlot = fuelSlot;
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
        return ItemContentRegistries.FUEL_TIME.get(this.getStack(fuelSlot).getItem()).orElse(0) > 0 || this.burnTime > 0;
    }

    @Override
    public void onBeginProcessing() {
        if (this.burnTime == 0) {
            this.tryBurnItemOrCancelRecipe();
        }
    }

    public void tryBurnItemOrCancelRecipe() {
        Optional<Integer> fuelTime = ItemContentRegistries.FUEL_TIME.get(this.getStack(fuelSlot).getItem());

        if (fuelTime.isPresent()) {
            this.getStack(fuelSlot).decrement(1);
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
}
