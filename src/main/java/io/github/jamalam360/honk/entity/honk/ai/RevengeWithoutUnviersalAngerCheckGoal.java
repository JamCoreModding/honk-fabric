package io.github.jamalam360.honk.entity.honk.ai;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class RevengeWithoutUnviersalAngerCheckGoal extends TrackTargetGoal {

    private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
    private int lastAttackedTime;

    public RevengeWithoutUnviersalAngerCheckGoal(PathAwareEntity mob) {
        super(mob, true);
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        int i = this.mob.getLastAttackedTime();
        LivingEntity livingEntity = this.mob.getAttacker();

        if (i != this.lastAttackedTime && livingEntity != null) {
            return this.canTrack(livingEntity, VALID_AVOIDABLES_PREDICATE);
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getAttacker());
        this.target = this.mob.getTarget();
        this.lastAttackedTime = this.mob.getLastAttackedTime();
        this.maxTimeWithoutVisibility = 300;
        super.start();
    }
}
