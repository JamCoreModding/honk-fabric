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

package io.github.jamalam360.honk.entity.egg;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class EggEntityModel extends EntityModel<EggEntity> {

    private final ModelPart base;

    public EggEntityModel(ModelPart root) {
        this.base = root.getChild(EntityModelPartNames.BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(
              EntityModelPartNames.BODY,
              ModelPartBuilder.create()
                    .uv(6, 8).cuboid(-2.0F, -8.0F, -3.0F, 4.0F, 7.0F, 6.0F, new Dilation(0.0F))
                    .uv(3, 6).cuboid(-3.0F, -8.0F, -2.0F, 6.0F, 7.0F, 4.0F, new Dilation(0.0F))
                    .uv(3, 6).cuboid(-2.0F, -9.0F, -2.0F, 4.0F, 9.0F, 4.0F, new Dilation(0.0F))
                    .uv(6, 8).cuboid(2.0F, -7.0F, -3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                    .uv(10, 6).cuboid(-3.0F, -7.0F, 2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                    .uv(7, 6).cuboid(-3.0F, -7.0F, -3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                    .uv(3, 6).cuboid(2.0F, -7.0F, 2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)),
              ModelTransform.pivot(0.0F, 24.0F, 0.0F)
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(EggEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.base.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
