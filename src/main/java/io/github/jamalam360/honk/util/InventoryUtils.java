package io.github.jamalam360.honk.util;

import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static boolean canStack(ItemStack stack, ItemStack query) {
        if (stack.isEmpty()) return true;
        if (stack.getItem() != query.getItem()) return false;

        return stack.getCount() + query.getCount() <= stack.getMaxCount();
    }
}
