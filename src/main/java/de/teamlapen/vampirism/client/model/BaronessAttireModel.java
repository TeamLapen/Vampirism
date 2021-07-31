package de.teamlapen.vampirism.client.model;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

/**
 * Attire designed for the female vampire baroness - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronessAttireModel extends EntityModel<VampireBaronEntity> {
    public ModelRenderer dressTorso;
    public ModelRenderer dressArmBandRight;
    public ModelRenderer dressArmBandLeft;
    public ModelRenderer hat;
    public ModelRenderer hood;

    public ModelRenderer dressCurtain;
    public ModelRenderer hat2;
    public ModelRenderer veil;
    public ModelRenderer cloak;

    private float enragedProgress = 0;

    public BaronessAttireModel() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.veil = new ModelRenderer(this, 32, 28);
        this.veil.setPos(0.0F, 0.0F, 0.0F);
        this.veil.addBox(-4.5F, -8.5F, -4.5F, 9, 9, 9, 0.0F);
        this.dressArmBandLeft = new ModelRenderer(this, 60, 46);
        this.dressArmBandLeft.mirror = true;
        this.dressArmBandLeft.setPos(4.0F, 2.0F, 0.0F);
        this.dressArmBandLeft.addBox(0.0F, 2.0F, -2.0F, 3, 3, 4, 0.5F);
        this.dressCurtain = new ModelRenderer(this, 64, 43);
        this.dressCurtain.setPos(0.0F, 12.0F, 0.0F);
        this.dressCurtain.addBox(-6.0F, 0.0F, -4.0F, 12, 11, 10, 0.0F);
        this.hood = new ModelRenderer(this, 44, 0);
        this.hood.setPos(0.0F, 0.0F, 0.0F);
        this.hood.addBox(-4.5F, -8.5F, -4.0F, 9, 9, 9, 0.0F);

        this.hat2 = new ModelRenderer(this, 72, 30);
        this.hat2.setPos(0.0F, 0.0F, 0.0F);
        this.hat2.addBox(-2.0F, -11.0F, -2.0F, 4, 2, 4, 0.0F);
        this.dressTorso = new ModelRenderer(this, 36, 46);
        this.dressTorso.setPos(0.0F, 0.0F, 0.0F);
        this.dressTorso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.4F);
        this.hat = new ModelRenderer(this, 68, 36);
        this.hat.setPos(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-3.0F, -9.0F, -3.0F, 6, 1, 6, 0.0F);

        this.cloak = new ModelRenderer(this, 0, 0);
        this.cloak.setPos(0.0F, 0.0F, 0.0F);
        this.cloak.addBox(-8.5F, -1.0F, -2.5F, 17, 22, 5, 0.0F);
        this.setRotateAngle(cloak, 0.3141592653589793F, 0.0F, 0.0F);
        this.dressArmBandRight = new ModelRenderer(this, 60, 46);
        this.dressArmBandRight.setPos(-4.0F, 2.0F, 0.0F);
        this.dressArmBandRight.addBox(-3.0F, 2.0F, -2.0F, 3, 3, 4, 0.5F);
        this.hat.addChild(this.veil);
        this.dressTorso.addChild(this.dressCurtain);
        this.hat.addChild(this.hat2);
        this.hood.addChild(this.cloak);
    }

    @Override
    public void prepareMobModel(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        enragedProgress = entityIn.getEnragedProgress();
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.dressArmBandLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.dressArmBandRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.dressTorso.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.hat.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pushPose();
        matrixStackIn.scale(1 - 0.5f * enragedProgress, 1 - 0.7f * enragedProgress, 1 - 0.5f * enragedProgress);
        this.hood.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer renderer, float x, float y, float z) {
        renderer.xRot = x;
        renderer.yRot = y;
        renderer.zRot = z;
    }

    @Override
    public void setupAnim(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyRotateY = 0;
        float headRotateY = 0;
        headRotateY = netHeadYaw * ((float) Math.PI / 180f);
        if (this.attackTime > 0.0F) {
            HandSide handside = this.getSwingingSide(entityIn);
            float f1 = this.attackTime;
            bodyRotateY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                bodyRotateY *= -1.0F;
            }
        }

        this.hat.yRot = bodyRotateY + headRotateY;
        this.veil.yRot = bodyRotateY + headRotateY;
        this.hood.yRot = bodyRotateY + headRotateY;
        this.hat2.yRot = bodyRotateY + headRotateY;
        this.cloak.yRot = bodyRotateY;
        this.dressCurtain.yRot = bodyRotateY;
        this.dressTorso.yRot = bodyRotateY;
        this.dressArmBandLeft.yRot = bodyRotateY;
        this.dressArmBandRight.yRot = bodyRotateY;
    }

    protected HandSide getSwingingSide(VampireBaronEntity entity) {
        HandSide handside = entity.getMainArm();
        return entity.swingingArm == Hand.MAIN_HAND ? handside : handside.getOpposite();
    }
}