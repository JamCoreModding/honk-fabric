package io.github.jamalam360.honk.mixin;

import io.github.jamalam360.honk.item.syringe.BloodSyringeItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
          method = "applyFoodEffects",
          at = @At("HEAD"),
          cancellable = true
    )
    private void honk$applyBloodEffects(ItemStack stack, World world, LivingEntity targetEntity, CallbackInfo ci) {
        if (stack.getItem() instanceof BloodSyringeItem && !world.isClient) {
            targetEntity.addStatusEffect(BloodSyringeItem.getStatusEffect(stack));
            ci.cancel();
        }
    }
}
