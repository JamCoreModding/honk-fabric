package io.github.jamalam360.honk.entity.honk;

import io.github.jamalam360.honk.HonkClientInit;
import io.github.jamalam360.honk.HonkInit;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HonkEntityRenderer extends MobEntityRenderer<HonkEntity, HonkEntityModel> {
    public HonkEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HonkEntityModel(context.getPart(HonkClientInit.HONK_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(HonkEntity entity) {
        return HonkInit.id("textures/entity/egg/egg.png");
    }

    @Override
    public void render(HonkEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float scale = Math.max(0.5F, Math.min(1.5F, (float) (1 + mobEntity.getBlendedSizeModifier())));
        matrixStack.scale(scale, scale, scale);
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    protected float getAnimationProgress(HonkEntity entity, float tickDelta) {
        float g = MathHelper.lerp(tickDelta, entity.prevFlapProgress, entity.flapProgress);
        float h = MathHelper.lerp(tickDelta, entity.prevMaxWingDeviation, entity.maxWingDeviation);
        return (MathHelper.sin(g) + 1.0F) * h;
    }
}

