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

    public static final MapCodec<HonkType> CODEC = RecordCodecBuilder.mapCodec(instance ->
          instance.group(
                Codec.INT.fieldOf("tier").forGetter(HonkType::tier),
                Identifier.CODEC.fieldOf("output").forGetter((type) -> Registry.ITEM.getId(type.output().getItem()))
          ).apply(instance, (tier, identifier) -> new HonkType(tier, Registry.ITEM.get(identifier).getDefaultStack()))
    );
    public static final Map<Identifier, HonkType> ENTRIES = new HashMap<>();
    private static final Random RANDOM = new Random();

    public Identifier id() {
        for (Identifier id : ENTRIES.keySet()) {
            if (ENTRIES.get(id).equals(this)) {
                return id;
            }
        }

        return null;
    }

    public static HonkType getRandom(int tier) {
        var types = ENTRIES.entrySet().stream().filter((o) -> o.getValue().tier() == tier).toArray();
        return (HonkType) types[RANDOM.nextInt(types.length)];
    }

}
