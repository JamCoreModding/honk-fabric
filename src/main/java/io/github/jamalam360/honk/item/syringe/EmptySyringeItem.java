package io.github.jamalam360.honk.item.syringe;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.data.rea.EntityBloodData;
import io.github.jamalam360.honk.registry.HonkData;
import io.github.jamalam360.honk.registry.HonkItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class EmptySyringeItem extends Item {

    public EmptySyringeItem() {
        super(new QuiltItemSettings().group(HonkInit.GROUP).maxCount(1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.world.isClient) {
            return ActionResult.SUCCESS;
        }

        EntityBloodData bloodData = HonkData.ENTITY_BLOOD_DATA.getNullable(entity.getType());
        // There's a default
        assert bloodData != null;

        entity.damage(DamageSource.player(user), 2);
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 180));

        if (!bloodData.extractable().orElse(true) || user.world.random.nextFloat() < (bloodData.breakChance().orElse(0.2F) + this.getBreakChanceModifier())) {
            user.playSound(SoundEvents.ENTITY_ITEM_BREAK);
            user.setStackInHand(hand, ItemStack.EMPTY);
            return ActionResult.SUCCESS;
        }

        ItemStack syringe = HonkItems.BLOOD_SYRINGE.getDefaultStack();
        BloodSyringeItem.write(syringe, (EntityType<? extends LivingEntity>) entity.getType(), bloodData);

        if (stack.hasCustomName()) {
            syringe.setCustomName(stack.getName());
        }

        user.playSound(SoundEvents.ENTITY_GENERIC_DRINK);
        user.setStackInHand(hand, syringe);

        return ActionResult.SUCCESS;
    }

    public float getBreakChanceModifier() {
        return 0F;
    }
}
