package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClothingCrownModel extends VampirismArmorModel {

    private static ClothingCrownModel instance;

    public static ClothingCrownModel getInstance() {
        if (instance == null) {
            instance = new ClothingCrownModel();
        }
        return instance;
    }

    public ModelRenderer front;
    public ModelRenderer back;
    public ModelRenderer left;
    public ModelRenderer right;

    public ClothingCrownModel() {
        super(64,32);
        this.left = new ModelRenderer(this, 0, 0);
        this.left.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.left.setTextureOffset(0, 6).addBox(-5.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, -0.2F, 0.1F, 0.1F);
        this.left.setTextureOffset(12, 17).addBox(-5.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.left.setTextureOffset(0, 17).addBox(-5.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.left.setTextureOffset(6, 17).addBox(-5.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, 0.1F);
        this.left.setTextureOffset(13, 15).addBox(-5.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.left.setTextureOffset(1, 15).addBox(-5.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.left.setTextureOffset(7, 15).addBox(-5.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.05F);
        this.front = new ModelRenderer(this, 0, 0);
        this.front.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.front.setTextureOffset(0, 4).addBox(-4.0F, -7.7F, -5.0F, 8.0F, 1.0F, 1.0F, 0.1F, 0.1F, -0.2F);
        this.front.setTextureOffset(0, 2).addBox(-3.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.setTextureOffset(12, 2).addBox(1.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.setTextureOffset(6, 2).addBox(-1.0F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, 0.1F, 0.0F, -0.2F);
        this.front.setTextureOffset(1, 0).addBox(-3.25F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.setTextureOffset(13, 0).addBox(2.2F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.setTextureOffset(7, 0).addBox(-0.5F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, -0.05F, 0.0F, -0.2F);
        this.back = new ModelRenderer(this, 0, 0);
        this.back.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.back.setTextureOffset(18, 4).addBox(-4.0F, -7.7F, 4.0F, 8.0F, 1.0F, 1.0F, 0.1F, 0.1F, -0.2F);
        this.back.setTextureOffset(30, 2).addBox(-3.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.setTextureOffset(18, 2).addBox(1.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.setTextureOffset(24, 2).addBox(-1.0F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, 0.1F, 0.0F, -0.2F);
        this.back.setTextureOffset(31, 0).addBox(-3.25F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.setTextureOffset(19, 0).addBox(2.2F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.setTextureOffset(25, 0).addBox(-0.5F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, -0.05F, 0.0F, -0.2F);
        this.right = new ModelRenderer(this, 0, 0);
        this.right.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.right.setTextureOffset(18, 6).addBox(4.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, -0.2F, 0.1F, 0.1F);
        this.right.setTextureOffset(18, 17).addBox(4.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.right.setTextureOffset(30, 17).addBox(4.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.right.setTextureOffset(24, 17).addBox(4.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, 0.1F);
        this.right.setTextureOffset(19, 15).addBox(4.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.right.setTextureOffset(31, 15).addBox(4.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.right.setTextureOffset(25, 15).addBox(4.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.05F);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadModels() {
        return ImmutableList.of(this.front, this.back, this.right, this.left);
    }
}
