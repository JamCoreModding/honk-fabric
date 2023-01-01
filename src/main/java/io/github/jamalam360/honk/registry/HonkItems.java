package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.item.AmberItem;
import io.github.jamalam360.honk.item.EggItem;
import io.github.jamalam360.honk.item.syringe.BloodSyringeItem;
import io.github.jamalam360.honk.item.syringe.EmptySyringeItem;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.item.Item;

@SuppressWarnings("unused")
@ContentRegistry(HonkInit.MOD_ID)
public class HonkItems {

    public static final Item AMBER = new AmberItem();
    public static final Item EGG = new EggItem();

    public static final Item EMPTY_SYRINGE = new EmptySyringeItem();
    //    public static final Item DIAMOND_EMPTY_SYRINGE = new DiamondEmptySyringe();
    public static final Item BLOOD_SYRINGE = new BloodSyringeItem();
}
