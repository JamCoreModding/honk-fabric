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

package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.entity.egg.EggEntity;
import io.github.jamalam360.honk.entity.honk.HonkEntity;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;
import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;

import java.util.Optional;

@ContentRegistry(HonkInit.MOD_ID)
public class HonkEntities {

	public static final TrackedDataHandler<Optional<Integer>> OPTIONAL_INTEGER = TrackedDataHandler.createOptional(PacketByteBuf::getInt, PacketByteBuf::readInt);
	public static final EntityType<EggEntity> EGG = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, EggEntity::new).setDimensions(EntityDimensions.fixed(6f / 16f, 8f / 16f)).build();
	public static final EntityType<HonkEntity> HONK = QuiltEntityTypeBuilder.create(SpawnGroup.CREATURE, HonkEntity::new).setDimensions(EntityDimensions.changing(0.8f, 0.8f)).build();

	static {
		QuiltTrackedDataHandlerRegistry.register(HonkInit.idOf("optional_integer"), OPTIONAL_INTEGER);
	}
}
