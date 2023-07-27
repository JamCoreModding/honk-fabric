package io.github.jamalam360.honk.block;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class AbstractBlockEntityWithInventory extends BlockEntity implements SidedInventory {

	public final DefaultedList<ItemStack> inventory;
	public final InventoryStorage storage;

	public AbstractBlockEntityWithInventory(BlockEntityType<?> type, int inventorySize, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
		this.storage = InventoryStorage.of(this, Direction.UP);
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
			this.markDirty();
		}

		return result;
	}

	@Override
	public ItemStack removeStack(int slot) {
		this.markDirty();
		return Inventories.removeStack(this.inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory.set(slot, stack);
		if (stack.getCount() > getMaxCountPerStack()) {
			stack.setCount(getMaxCountPerStack());
		}

		this.markDirty();
	}

	@Override
	public void clear() {
		this.inventory.clear();
		this.markDirty();
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
