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
import io.github.jamalam360.honk.HonkInit;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

public record HonkType(
		int tier,
		String name,
		List<List<Identifier>> parents,
		Optional<Identifier> texture,
		ItemStack output
) {
	public static final Map<String, HonkType> ENTRIES = new HashMap<>();
	public static final MapCodec<HonkType> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					Codec.INT.fieldOf("tier").forGetter(HonkType::tier),
					Codec.STRING.fieldOf("name").forGetter(HonkType::name),
					Codec.list(Codec.list(Identifier.CODEC)).fieldOf("parents").forGetter(HonkType::parents),
					Codec.optionalField("texture", Identifier.CODEC).forGetter(HonkType::texture),
					Identifier.CODEC.fieldOf("output").forGetter((type) -> Registries.ITEM.getId(type.output().getItem()))
			).apply(instance, (tier, name, parents, texture, identifier) -> new HonkType(tier, name, parents, texture, Registries.ITEM.get(identifier).getDefaultStack()))
	);
	private static final Random RANDOM = new Random();

	public static HonkType getRandom(int tier) {
		List<HonkType> types = ENTRIES.values().stream().filter((o) -> o.tier() == tier).toList();
		return types.get(RANDOM.nextInt(types.size()));
	}

	public static HonkType fromPacket(PacketByteBuf buf) {
		int tier = buf.readInt();
		String name = buf.readString();
		int parentsSize = buf.readInt();
		List<List<Identifier>> parents = new ArrayList<>();
		for (int i = 0; i < parentsSize; i++) {
			int parentSize = buf.readInt();
			List<Identifier> parent = new ArrayList<>();
			for (int j = 0; j < parentSize; j++) {
				parent.add(buf.readIdentifier());
			}
			parents.add(parent);
		}
		Optional<Identifier> texture = buf.readBoolean() ? Optional.of(buf.readIdentifier()) : Optional.empty();
		ItemStack output = buf.readItemStack();
		return new HonkType(tier, name, parents, texture, output);
	}

	public String id() {
		for (String id : ENTRIES.keySet()) {
			if (ENTRIES.get(id).equals(this)) {
				return id;
			}
		}

		return null;
	}

	public Identifier getTexture() {
		if (texture().isPresent()) {
			return texture().get();
		} else {
			return HonkInit.idOf("textures/entity/honk/" + Registries.ITEM.getId(this.output.getItem()).getPath() + ".png");
		}
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeString(this.id());
		buf.writeInt(tier);
		buf.writeString(name);
		buf.writeInt(parents.size());
		for (List<Identifier> parent : parents) {
			buf.writeInt(parent.size());
			for (Identifier identifier : parent) {
				buf.writeIdentifier(identifier);
			}
		}
		buf.writeBoolean(texture.isPresent());
		if (texture.isPresent()) {
			buf.writeIdentifier(texture.get());
		}
		buf.writeItemStack(output);
	}
}
