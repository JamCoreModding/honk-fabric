package io.github.jamalam360.honk.entity.honk.ai;

import io.github.jamalam360.honk.entity.honk.HonkEntity;
import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class MoveTowardsOtherHonksOfSameTypeGoal extends Goal {

    private final HonkEntity honk;
    private final double speed;
    @Nullable
    private HonkEntity other;
    private int delay;

    public MoveTowardsOtherHonksOfSameTypeGoal(HonkEntity honk, double speed) {
        this.honk = honk;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        List<HonkEntity> others = this.honk.getWorld().getEntitiesByClass(HonkEntity.class, this.createBox(), (honk) -> honk != this.honk && honk.getHonkType().id().equals(this.honk.getHonkType().id()));

        if (others.size() > 0) {
            this.other = others.get(this.honk.getWorld().getRandom().nextInt(others.size()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.other == null) {
            return false;
        } else {
            double d = this.honk.squaredDistanceTo(this.other);
            return !(d < 9.0) && !(d > 256.0);
        }
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void stop() {
        this.other = null;
    }

    @Override
    public void tick() {
        if (--this.delay <= 0) {
            this.delay = this.getTickCount(10);
            this.honk.getNavigation().startMovingTo(this.other, this.speed);
        }
    }

    private Box createBox() {
        return Box.of(this.honk.getPos(), 16, 3, 16);
    }
}
