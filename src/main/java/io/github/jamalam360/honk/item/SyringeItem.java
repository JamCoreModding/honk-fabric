/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.honk.item;

import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.honk.registry.HonkItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class SyringeItem extends Item {

    public SyringeItem() {
        super(new QuiltItemSettings().maxCount(16).recipeSelfRemainder());
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof HonkEntity honk && !user.getWorld().isClient) {
            DnaData data = DnaData.fromEntity(honk);
            ItemStack bloodSyringe = HonkItems.BLOOD_SYRINGE.getDefaultStack();
            bloodSyringe.setNbt(data.writeNbt(bloodSyringe.getOrCreateNbt()));
            entity.damage(entity.getDamageSources().playerAttack(user), 2.0F);
            user.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1.0F, user.getWorld().random.nextFloat() + 0.5F);
            user.setStackInHand(hand, bloodSyringe);
            return ActionResult.SUCCESS;
        } else {
            return super.useOnEntity(stack, user, entity, hand);
        }
    }
}
