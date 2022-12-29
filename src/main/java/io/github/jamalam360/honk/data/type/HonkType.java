package io.github.jamalam360.honk.data.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record HonkType(
      int tier,
      ItemStack output
) {

    private static final Random RANDOM = new Random();
    public static final Map<Identifier, HonkType> ENTRIES = new HashMap<>();
    public static final MapCodec<HonkType> CODEC = RecordCodecBuilder.mapCodec(instance ->
          instance.group(
                Codec.INT.fieldOf("tier").forGetter(HonkType::tier),
                Identifier.CODEC.fieldOf("output").forGetter((type) -> Registry.ITEM.getId(type.output().getItem()))
          ).apply(instance, (tier, identifier) -> new HonkType(tier, Registry.ITEM.get(identifier).getDefaultStack()))
    );

    public static HonkType getRandom(int tier) {
        return (HonkType) ENTRIES.values().stream().filter((o) -> o.tier() == tier).toArray()[RANDOM.nextInt(ENTRIES.size())];
    }

    public Identifier id() {
        for (Map.Entry<Identifier, HonkType> e : ENTRIES.entrySet()) {
            if (e.getValue().equals(this)) {
                return e.getKey();
            }
        }

        return null;
    }
}
