package io.github.jamalam360.honk.entity.honk.ai;

import java.util.Map;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class JumpTask extends Task<MobEntity> {

    private boolean hasJumped = false;

    public JumpTask() {
        super(Map.of());
    }

    @Override
    protected void run(ServerWorld world, MobEntity entity, long time) {
        if (entity.isOnGround()) {
            this.hasJumped = true;
            entity.getJumpControl().setActive();
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, MobEntity entity, long time) {
        return !this.hasJumped;
    }
}
