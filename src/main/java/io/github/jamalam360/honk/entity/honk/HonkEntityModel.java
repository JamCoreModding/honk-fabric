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

package io.github.jamalam360.honk.entity.honk;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class HonkEntityModel extends EntityModel<HonkEntity> {

	private final ModelPart head;
	private final ModelPart beak;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart body;

	public HonkEntityModel(ModelPart root) {
		this.head = root.getChild("head");
		this.beak = root.getChild("beak");
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");
		this.leftWing = root.getChild("left_wing");
		this.rightWing = root.getChild("right_wing");
		this.body = root.getChild("body");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("head", ModelPartBuilder.create().uv(22, 24).cuboid(-2.0F, -11.0F, -2.0F, 4.0F, 11.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, -4.0F));
		modelPartData.addChild("beak", ModelPartBuilder.create().uv(12, 22).cuboid(-2.0F, -9.0F, -4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, -4.0F));
		modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 34).cuboid(-2.0F, 0.0F, -1.0F, 3.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 19.0F, 1.0F));
		modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 19.0F, 2.0F));
		modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(22, 10).cuboid(-1.0F, 0.0F, -5.0F, 1.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 13.0F, 2.0F));
		modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(0, 20).cuboid(0.0F, 0.0F, -5.0F, 1.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 13.0F, 2.0F));
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -5.0F, 21.0F, 10.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 40.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(HonkEntity entity, float limbAngle, float limbDistance, float animationProgress, float netHeadYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = netHeadYaw * (float) (Math.PI / 180.0);
		this.beak.pitch = this.head.pitch;
		this.beak.yaw = this.head.yaw;
		this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.rightWing.roll = animationProgress;
		this.leftWing.roll = -animationProgress;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		this.head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.beak.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.leftLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.rightLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.rightWing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.leftWing.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
