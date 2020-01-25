package de.teamlapen.vampirism.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * ModelBiped with a cloak
 */
@OnlyIn(Dist.CLIENT)
public class BipedCloakedModel<T extends LivingEntity> extends BipedModel<T> {
    protected ModelRenderer bipedCloak;

    public BipedCloakedModel(float f1, float f2, int texWidth, int texHeight) {
        this(f1, f2, texWidth, texHeight, 65, 0);
    }

    public BipedCloakedModel(float f1, float f2, int texWidth, int texHeight, int capeX, int capeY) {
        super(f1, f2, texWidth, texHeight);
        bipedCloak = new ModelRenderer(this, capeX, capeY);
        bipedCloak.addBox(-7.0F, 0.0F, 0.4F, 14, 20, 1);
        bipedCloak.setRotationPoint(0, 0, 2);
    }

    @Override
    public void render(T entity, float f, float f1, float f2, float f3, float f4) {
        super.render(entity, f, f1, f2, f3, f4);
        this.bipedCloak.copyModelAngles(this.bipedBody);

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        bipedCloak.showModel = visible;
    }
}
