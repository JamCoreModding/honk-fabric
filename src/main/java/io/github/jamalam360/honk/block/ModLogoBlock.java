package io.github.jamalam360.honk.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equippable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class ModLogoBlock extends Block implements Equippable {

    public ModLogoBlock() {
        super(QuiltBlockSettings.copy(Blocks.WHITE_WOOL));
    }

    @Override
    public EquipmentSlot getPreferredSlot() {
        return EquipmentSlot.HEAD;
    }
}
