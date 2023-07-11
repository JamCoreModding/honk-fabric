package io.github.jamalam360.honk.entity.honk.ai;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class EscapeDangerIfTooHurtGoal extends EscapeDangerGoal {

    public EscapeDangerIfTooHurtGoal(PathAwareEntity mob, double speed) {
        super(mob, speed);
    }

    @Override
    protected boolean shouldEscape() {
        return super.shouldEscape() && this.mob.getHealth() < this.mob.getMaxHealth() / 3;
    }
}
