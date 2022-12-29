package io.github.jamalam360.honk.item;

import io.github.jamalam360.honk.HonkInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class AmberItem extends Item {

    public AmberItem() {
        super(new QuiltItemSettings().group(HonkInit.GROUP));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        // TODO: Write this with one of the base Honk types
        return stack;
    }
}
