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

package io.github.jamalam360.honk.entity.honk.ai;

import io.github.jamalam360.honk.entity.honk.HonkEntity;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HonkMateGoal extends Goal {

    private static final TargetPredicate VALID_MATE_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility();
    protected final HonkEntity honk;
    protected final World world;
    private final double chance;
    @Nullable
    protected HonkEntity mate;
    private int timer;


    public HonkMateGoal(HonkEntity honk, double chance) {
        this.honk = honk;
        this.world = honk.getWorld();
        this.chance = chance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.honk.isInLove()) {
            return false;
        } else {
            this.mate = this.findMate();
            return this.mate != null;
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.mate.isAlive() && this.mate.isInLove() && this.timer < 60;
    }

    @Override
    public void stop() {
        this.mate = null;
        this.timer = 0;
    }

    @Override
    public void tick() {
        this.honk.getLookControl().lookAt(this.mate, 10.0F, (float) this.honk.getLookPitchSpeed());
        this.honk.getNavigation().startMovingTo(this.mate, this.chance);
        ++this.timer;
        if (this.timer >= this.getTickCount(60) && this.honk.squaredDistanceTo(this.mate) < 9.0) {
            this.breed();
        }
    }

    @Nullable
    private HonkEntity findMate() {
        List<? extends HonkEntity> entities = this.world.getTargets(HonkEntity.class, VALID_MATE_PREDICATE, this.honk, this.honk.getBoundingBox().expand(8.0));
        double d = Double.MAX_VALUE;
        HonkEntity other = null;

        for (HonkEntity potentialMate : entities) {
            if (this.honk.canBreedWith(potentialMate) && this.honk.squaredDistanceTo(potentialMate) < d) {
                other = potentialMate;
                d = this.honk.squaredDistanceTo(potentialMate);
            }
        }

        return other;
    }

    protected void breed() {
        this.honk.breed((ServerWorld) this.world, this.mate);
    }
}
