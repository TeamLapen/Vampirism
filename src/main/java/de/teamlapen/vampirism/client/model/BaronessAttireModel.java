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
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.veil = new ModelRenderer(this, 32, 28);
        this.veil.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.veil.addBox(-4.5F, -8.5F, -4.5F, 9, 9, 9, 0.0F);
        this.dressArmBandLeft = new ModelRenderer(this, 60, 46);
        this.dressArmBandLeft.mirror = true;
        this.dressArmBandLeft.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.dressArmBandLeft.addBox(0.0F, 2.0F, -2.0F, 3, 3, 4, 0.5F);
        this.dressCurtain = new ModelRenderer(this, 64, 43);
        this.dressCurtain.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.dressCurtain.addBox(-6.0F, 0.0F, -4.0F, 12, 11, 10, 0.0F);
        this.hood = new ModelRenderer(this, 44, 0);
        this.hood.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hood.addBox(-4.5F, -8.5F, -4.0F, 9, 9, 9, 0.0F);

        this.hat2 = new ModelRenderer(this, 72, 30);
        this.hat2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat2.addBox(-2.0F, -11.0F, -2.0F, 4, 2, 4, 0.0F);
        this.dressTorso = new ModelRenderer(this, 36, 46);
        this.dressTorso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.dressTorso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.4F);
        this.hat = new ModelRenderer(this, 68, 36);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-3.0F, -9.0F, -3.0F, 6, 1, 6, 0.0F);

        this.cloak = new ModelRenderer(this, 0, 0);
        this.cloak.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cloak.addBox(-8.5F, -1.0F, -2.5F, 17, 22, 5, 0.0F);
        this.setRotateAngle(cloak, 0.3141592653589793F, 0.0F, 0.0F);
        this.dressArmBandRight = new ModelRenderer(this, 60, 46);
        this.dressArmBandRight.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.dressArmBandRight.addBox(-3.0F, 2.0F, -2.0F, 3, 3, 4, 0.5F);
        this.hat.addChild(this.veil);
        this.dressTorso.addChild(this.dressCurtain);
        this.hat.addChild(this.hat2);
        this.hood.addChild(this.cloak);
    }

    @Override
    public void setRotationAngles(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyRotateY = 0;
        float headRotateY = 0;
        headRotateY = netHeadYaw * ((float) Math.PI / 180f);
        if (this.swingProgress > 0.0F) {
            HandSide handside = this.getSwingingSide(entityIn);
            float f1 = this.swingProgress;
            bodyRotateY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                bodyRotateY *= -1.0F;
            }
        }

        this.hat.rotateAngleY = bodyRotateY + headRotateY;
        this.veil.rotateAngleY = bodyRotateY + headRotateY;
        this.hood.rotateAngleY = bodyRotateY + headRotateY;
        this.hat2.rotateAngleY = bodyRotateY + headRotateY;
        this.cloak.rotateAngleY = bodyRotateY;
        this.dressCurtain.rotateAngleY = bodyRotateY;
        this.dressTorso.rotateAngleY = bodyRotateY;
        this.dressArmBandLeft.rotateAngleY = bodyRotateY;
        this.dressArmBandRight.rotateAngleY = bodyRotateY;
    }




    @Override
    public void setLivingAnimations(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        enragedProgress = entityIn.getEnragedProgress();
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.dressArmBandLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.dressArmBandRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.dressTorso.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.hat.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.push();
        matrixStackIn.scale(1-0.5f*enragedProgress,1-0.7f*enragedProgress, 1-0.5f*enragedProgress);
        this.hood.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer renderer, float x, float y, float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }


    protected HandSide getSwingingSide(VampireBaronEntity entity) {
        HandSide handside = entity.getPrimaryHand();
        return entity.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }
}