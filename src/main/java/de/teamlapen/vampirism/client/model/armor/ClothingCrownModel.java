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
        super(64, 32);
        this.left = new ModelRenderer(this, 0, 0);
        this.left.setPos(0.0F, 0.0F, 0.0F);
        this.left.texOffs(0, 6).addBox(-5.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, -0.2F, 0.1F, 0.1F);
        this.left.texOffs(12, 17).addBox(-5.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.left.texOffs(0, 17).addBox(-5.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.left.texOffs(6, 17).addBox(-5.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, 0.1F);
        this.left.texOffs(13, 15).addBox(-5.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.left.texOffs(1, 15).addBox(-5.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.left.texOffs(7, 15).addBox(-5.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.05F);
        this.front = new ModelRenderer(this, 0, 0);
        this.front.setPos(0.0F, 0.0F, 0.0F);
        this.front.texOffs(0, 4).addBox(-4.0F, -7.7F, -5.0F, 8.0F, 1.0F, 1.0F, 0.1F, 0.1F, -0.2F);
        this.front.texOffs(0, 2).addBox(-3.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.texOffs(12, 2).addBox(1.7F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.texOffs(6, 2).addBox(-1.0F, -8.8F, -5.0F, 2.0F, 1.0F, 1.0F, 0.1F, 0.0F, -0.2F);
        this.front.texOffs(1, 0).addBox(-3.25F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.texOffs(13, 0).addBox(2.2F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.front.texOffs(7, 0).addBox(-0.5F, -9.8F, -5.0F, 1.0F, 1.0F, 1.0F, -0.05F, 0.0F, -0.2F);
        this.back = new ModelRenderer(this, 0, 0);
        this.back.setPos(0.0F, 0.0F, 0.0F);
        this.back.texOffs(18, 4).addBox(-4.0F, -7.7F, 4.0F, 8.0F, 1.0F, 1.0F, 0.1F, 0.1F, -0.2F);
        this.back.texOffs(30, 2).addBox(-3.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.texOffs(18, 2).addBox(1.7F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.texOffs(24, 2).addBox(-1.0F, -8.8F, 4.0F, 2.0F, 1.0F, 1.0F, 0.1F, 0.0F, -0.2F);
        this.back.texOffs(31, 0).addBox(-3.25F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.texOffs(19, 0).addBox(2.2F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, -0.1F, 0.0F, -0.2F);
        this.back.texOffs(25, 0).addBox(-0.5F, -9.8F, 4.0F, 1.0F, 1.0F, 1.0F, -0.05F, 0.0F, -0.2F);
        this.right = new ModelRenderer(this, 0, 0);
        this.right.setPos(0.0F, 0.0F, 0.0F);
        this.right.texOffs(18, 6).addBox(4.0F, -7.7F, -4.0F, 1.0F, 1.0F, 8.0F, -0.2F, 0.1F, 0.1F);
        this.right.texOffs(18, 17).addBox(4.0F, -8.8F, -3.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.right.texOffs(30, 17).addBox(4.0F, -8.8F, 1.7F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, -0.1F);
        this.right.texOffs(24, 17).addBox(4.0F, -8.8F, -1.0F, 1.0F, 1.0F, 2.0F, -0.2F, 0.0F, 0.1F);
        this.right.texOffs(19, 15).addBox(4.0F, -9.8F, -3.25F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.right.texOffs(31, 15).addBox(4.0F, -9.8F, 2.2F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.1F);
        this.right.texOffs(25, 15).addBox(4.0F, -9.8F, -0.5F, 1.0F, 1.0F, 1.0F, -0.2F, 0.0F, -0.05F);
        getHeadModels().forEach(this.head::addChild); //Make sure hierarchy is correct (e.g. for EpicFightMod)
    }

    @Override
    protected Iterable<ModelRenderer> getHeadModels() {
        return ImmutableList.of(this.front, this.back, this.right, this.left);
    }
}
