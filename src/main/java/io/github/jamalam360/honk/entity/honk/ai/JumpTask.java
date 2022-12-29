package io.github.jamalam360.honk.entity.honk.ai;

import java.util.Map;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class JumpTask extends Task<MobEntity> {


    public JumpTask() {
        super(Map.of());
    }

    @Override
    protected void run(ServerWorld world, MobEntity entity, long time) {
        super.run(world, entity, time);

        if (entity.isOnGround()) {
            entity.getJumpControl().setActive();
            this.stop(world, entity, time);
        }
    }
}
