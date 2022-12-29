package io.github.jamalam360.honk.entity.egg;

import io.github.jamalam360.honk.HonkClientInit;
import io.github.jamalam360.honk.HonkInit;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class EggEntityRenderer extends MobEntityRenderer<EggEntity, EggEntityModel> {
    public EggEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EggEntityModel(context.getPart(HonkClientInit.EGG_LAYER)), 0.25f);
    }

    @Override
    public Identifier getTexture(EggEntity entity) {
        return HonkInit.id("textures/entity/egg/egg.png");
    }

    @Override
    public void render(EggEntity mobEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrices.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(0, 0, mobEntity.getYaw())));
        super.render(mobEntity, f, g, matrices, vertexConsumerProvider, i);
    }
}
