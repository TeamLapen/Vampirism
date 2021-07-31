package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClothingBootsModel extends VampirismArmorModel {

    private static ClothingBootsModel instance;

    public static ClothingBootsModel getInstance() {
        if (instance == null) {
            instance = new ClothingBootsModel();
        }
        return instance;
    }

    public ModelRenderer rightBoot;
    public ModelRenderer leftBoot;
    public ModelRenderer leftToes;
    public ModelRenderer rightToes;

    public ClothingBootsModel() {
        super(32, 16);
        this.rightBoot = new ModelRenderer(this, 0, 0);
        this.rightBoot.setPos(1.9F, 12.0F, 0.0F);
        this.rightBoot.addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.4F, 0.4F, 0.4F);
        this.rightToes = new ModelRenderer(this, 2, 9);
        this.rightToes.setPos(1.9F, 12.0F, 0.0F);
        this.rightToes.addBox(-2.0F, 10.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.4F, 0.4F, 0.4F);
        this.leftBoot = new ModelRenderer(this, 16, 0);
        this.leftBoot.setPos(-1.9F, 12.0F, 0.0F);
        this.leftBoot.addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.4F, 0.4F, 0.4F);
        this.leftToes = new ModelRenderer(this, 18, 9);
        this.leftToes.setPos(-1.9F, 12.0F, 0.0F);
        this.leftToes.addBox(-2.0F, 10.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.4F, 0.4F, 0.4F);
    }

    @Override
    protected Iterable<ModelRenderer> getLeftLegModels() {
        return ImmutableList.of(this.leftBoot, this.leftToes);
    }

    @Override
    protected Iterable<ModelRenderer> getRightLegModels() {
        return ImmutableList.of(this.rightBoot, this.rightToes);
    }
}
