package io.github.jamalam360.honk.item.syringe;

import io.github.jamalam360.honk.data.rea.EntityBloodData;
import java.util.Optional;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

public class BloodSyringeItem extends Item {

    public static final String ENTITY_TYPE_KEY = "EntityType";
    public static final String EFFECT_KEY = "Effect";
    public static final String TRANSLATION_KEY_KEY = "TranslationKey";

    public BloodSyringeItem() {
        super(new QuiltItemSettings()
              .maxCount(1)
              .food(
                    new FoodComponent.Builder()
                          .alwaysEdible()
                          .hunger(1)
                          .build()
              )
        );
    }

    public static void write(ItemStack stack, EntityType<? extends LivingEntity> entity, EntityBloodData data) {
        NbtCompound nbt = new NbtCompound();
        stack.writeNbt(nbt);
        nbt.putString(ENTITY_TYPE_KEY, Registry.ENTITY_TYPE.getId(entity).toString());
        nbt.putString(EFFECT_KEY, data.effect().map((Identifier::toString)).orElse("default"));
        nbt.putString(TRANSLATION_KEY_KEY, data.translationKey().orElse(stack.getTranslationKey()));
        stack.setNbt(nbt);
    }

    public static StatusEffectInstance getStatusEffect(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        StatusEffect effect;

        if (nbt.getString(EFFECT_KEY).equals("default")) {
            effect = StatusEffects.NAUSEA;
        } else {
            effect = Registry.STATUS_EFFECT.get(new Identifier(nbt.getString(EFFECT_KEY)));
        }

        assert effect != null;
        return new StatusEffectInstance(effect, 200);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains(TRANSLATION_KEY_KEY)) {
            return super.getTranslationKey(stack);
        }

        return nbt.getString(TRANSLATION_KEY_KEY);
    }

    @ClientOnly
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.of((ConvertibleTooltipData) () ->
              TooltipComponent.of(
                    Text.translatable(
                          Registry.ENTITY_TYPE.get(
                                new Identifier(stack.getOrCreateNbt().getString(ENTITY_TYPE_KEY))
                          ).getTranslationKey()
                    ).formatted(Formatting.GRAY).asOrderedText()
              )
        );
    }
}
