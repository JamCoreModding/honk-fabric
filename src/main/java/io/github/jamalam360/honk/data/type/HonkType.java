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

package io.github.jamalam360.honk.data.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public record HonkType(
		int tier,
		String name,
		List<List<Identifier>> parents,
		Identifier texture,
		ItemStack output
) {

	public static final Map<String, HonkType> ENTRIES = new HashMap<>();
	public static final MapCodec<HonkType> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					Codec.INT.fieldOf("tier").forGetter(HonkType::tier),
					Codec.STRING.fieldOf("name").forGetter(HonkType::name),
					Codec.list(Codec.list(Identifier.CODEC)).fieldOf("parents").forGetter(HonkType::parents),
					Identifier.CODEC.fieldOf("texture").forGetter(HonkType::texture),
					Identifier.CODEC.fieldOf("output").forGetter((type) -> Registries.ITEM.getId(type.output().getItem()))
			).apply(instance, (tier, name, parents, texture, identifier) -> new HonkType(tier, name, parents, texture, Registries.ITEM.get(identifier).getDefaultStack()))
	);
	private static final Random RANDOM = new Random();

	public static HonkType getRandom(int tier) {
		List<HonkType> types = ENTRIES.values().stream().filter((o) -> o.tier() == tier).toList();
		return types.get(RANDOM.nextInt(types.size()));
	}

	public String id() {
		for (String id : ENTRIES.keySet()) {
			if (ENTRIES.get(id).equals(this)) {
				return id;
			}
		}

		return null;
	}

}
