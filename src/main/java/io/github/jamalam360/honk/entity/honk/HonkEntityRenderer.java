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

import io.github.jamalam360.honk.HonkClientInit;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HonkEntityRenderer extends MobEntityRenderer<HonkEntity, HonkEntityModel> {

	public HonkEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new HonkEntityModel(context.getPart(HonkClientInit.HONK_LAYER)), 0.5f);
		this.addFeature(new AngryEyebrowsFeatureRenderer(this));
	}

	@Override
	public Identifier getTexture(HonkEntity entity) {
		return entity.getHonkType().getTexture();
	}

	@Override
	public void render(HonkEntity entity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int l) {
		float scale = Math.max(0.5F, Math.min(1.5F, entity.getScaleFactor()));
		matrices.scale(scale, scale, scale);
		super.render(entity, f, g, matrices, vertexConsumerProvider, l);
	}

	@Override
	protected float getAnimationProgress(HonkEntity entity, float tickDelta) {
		float g = MathHelper.lerp(tickDelta, entity.prevFlapProgress, entity.flapProgress);
		float h = MathHelper.lerp(tickDelta, entity.prevMaxWingDeviation, entity.maxWingDeviation);
		return (MathHelper.sin(g) + 1.0F) * h;
	}
}

