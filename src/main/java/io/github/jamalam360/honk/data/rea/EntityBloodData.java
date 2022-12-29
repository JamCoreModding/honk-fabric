package io.github.jamalam360.honk.data.rea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.Identifier;

public record EntityBloodData(
      Optional<Float> breakChance,
      Optional<Boolean> extractable,
      Optional<Identifier> effect,
      Optional<String> translationKey
) {

    public static final MapCodec<EntityBloodData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
          .group(
                Codec.FLOAT.optionalFieldOf("breakChange").forGetter(EntityBloodData::breakChance),
                Codec.BOOL.optionalFieldOf("extractable").forGetter(EntityBloodData::extractable),
                Identifier.CODEC.optionalFieldOf("effect").forGetter(EntityBloodData::effect),
                Codec.STRING.optionalFieldOf("translationKey").forGetter(EntityBloodData::translationKey)
          )
          .apply(instance, EntityBloodData::new)
    );
}
