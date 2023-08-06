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

package io.github.jamalam360.honk.util;

import com.google.gson.JsonObject;
import io.github.jamalam360.honk.HonkInit;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.class_5258;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class HatchHonkCriterion extends AbstractCriterion<HatchHonkCriterion.Conditions> {
	public static final HatchHonkCriterion INSTANCE = new HatchHonkCriterion();
	private static final Identifier ID = HonkInit.idOf("hatch_honk");

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	protected Conditions conditionsFromJson(JsonObject json, class_5258 arg, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
		return new Conditions(this.getId());
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(Identifier id) {
			super(id, class_5258.field_24388);
		}
	}
}
