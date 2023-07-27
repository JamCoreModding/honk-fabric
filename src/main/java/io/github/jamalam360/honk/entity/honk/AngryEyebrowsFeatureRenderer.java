package io.github.jamalam360.honk.entity.honk;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import io.github.jamalam360.honk.HonkInit;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class AngryEyebrowsFeatureRenderer extends FeatureRenderer<HonkEntity, HonkEntityModel> {
	private static final Identifier TEXTURE = HonkInit.idOf("textures/entity/eyebrows.png");

	public AngryEyebrowsFeatureRenderer(FeatureRendererContext<HonkEntity, HonkEntityModel> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, HonkEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (false) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBufferBuilder();

			RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
			RenderSystem.setShaderTexture(0, TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);

			matrices.push();
			matrices.translate(0F, 0.25F, -0.3F);
			matrices.multiply(Axis.X_POSITIVE.rotationDegrees(headPitch));
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(headYaw));
			matrices.multiply(new Quaternionf(new AxisAngle4f((float) Math.PI, 0, 0, 1)));
			matrices.scale(0.5F, 0.5F, 0.5F);

			Matrix4f matrix4f = matrices.peek().getModel();
			buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);

			buffer.vertex(matrix4f, 0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).light(light).overlay(0)
					.next();

			buffer.vertex(matrix4f, 0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).light(light).overlay(0)
					.next();

			buffer.vertex(matrix4f, -0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).light(light).overlay(0)
					.next();

			buffer.vertex(matrix4f, -0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).light(light).overlay(0)
					.next();

			buffer.vertex(matrix4f, -0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).light(light).overlay(0)
					.next();
			buffer.vertex(matrix4f, -0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).light(light).overlay(0)
					.next();
			buffer.vertex(matrix4f, 0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).light(light).overlay(0)
					.next();
			buffer.vertex(matrix4f, 0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).light(light).overlay(0)
					.next();

			tessellator.draw();
			matrices.pop();
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
		}
	}
}
