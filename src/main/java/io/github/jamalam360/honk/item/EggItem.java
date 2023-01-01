package io.github.jamalam360.honk.item;

import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.registry.HonkEntities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class EggItem extends Item {

    public EggItem() {
        super(new QuiltItemSettings().maxCount(1));
    }

    public static void initializeFrom(ItemStack stack, EggEntity entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);
        stack.getOrCreateNbt().put("EggAttributes", nbt);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            EggEntity egg = new EggEntity(HonkEntities.EGG, context.getWorld());
            egg.readCustomDataFromNbt((NbtCompound) context.getStack().getNbt().get("EggAttributes"));
            egg.setPosition(context.getHitPos());
            context.getWorld().spawnEntity(egg);
            context.getPlayer().getStackInHand(context.getHand()).decrement(1);
        }

        return ActionResult.SUCCESS;
    }

    //TODO: Tooltips
}
