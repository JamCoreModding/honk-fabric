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
