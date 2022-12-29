package io.github.jamalam360.honk.entity.honk;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class HonkEntityModel extends EntityModel<HonkEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart bill;
    private final ModelPart chin;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public HonkEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.bill = this.head.getChild("bill");
        this.chin = this.head.getChild("chin");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.leftWing = this.root.getChild("left_wing");
        this.rightWing = this.root.getChild("right_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData honk = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        honk.addChild("right_leg", ModelPartBuilder.create().uv(32, 11).cuboid(0.0F, -1.0F, -2.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -5.0F, 1.0F));
        honk.addChild("left_leg", ModelPartBuilder.create().uv(32, 11).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -5.0F, 1.0F));
        honk.addChild("right_wing", ModelPartBuilder.create().uv(30, 24).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, -11.0F, 0.0F));
        honk.addChild("left_wing", ModelPartBuilder.create().uv(26, 17).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -11.0F, 0.0F));
        honk.addChild("body", ModelPartBuilder.create().uv(4, 11).cuboid(-4.0F, -5.0F, -4.0F, 7.0F, 9.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData head = honk.addChild("head", ModelPartBuilder.create().uv(6, 11).cuboid(-2.5F, -4.0F, -2.0F, 5.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -11.0F, -4.0F));
        head.addChild("bill", ModelPartBuilder.create().uv(20, 11).cuboid(-2.0F, -3.0F, -4.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 2.0F, 0.0F));
        head.addChild("chin", ModelPartBuilder.create().uv(17, 15).cuboid(-2.0F, -1.0F, -3.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 2.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(HonkEntity entity, float limbAngle, float limbDistance, float animationProgress, float netHeadYaw, float headPitch) {
        this.head.pitch = headPitch * (float) (Math.PI / 180.0);
        this.head.yaw = netHeadYaw * (float) (Math.PI / 180.0);
        this.bill.pitch = this.head.pitch;
        this.bill.yaw = this.head.yaw;
        this.chin.pitch = this.head.pitch;
        this.chin.yaw = this.head.yaw;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
        this.rightWing.roll = animationProgress;
        this.leftWing.roll = -animationProgress;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
