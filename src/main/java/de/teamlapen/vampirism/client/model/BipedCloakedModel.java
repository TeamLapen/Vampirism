package de.teamlapen.vampirism.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * ModelBiped with a cloak
 */
@OnlyIn(Dist.CLIENT)
public class BipedCloakedModel<T extends LivingEntity> extends BipedModel<T> {
    protected RendererModel bipedCloak;
    private boolean skipCloakOnce = false;

    public BipedCloakedModel(float f1, float f2, int texWidth, int texHeight) {
        this(f1, f2, texWidth, texHeight, 65, 0);
    }

    public BipedCloakedModel(float f1, float f2, int texWidth, int texHeight, int capeX, int capeY) {
        super(f1, f2, texWidth, texHeight);
        bipedCloak = new RendererModel(this, capeX, capeY);
        bipedCloak.addBox(-7.0F, 0.0F, 0.4F, 14, 20, 1);
        bipedCloak.setRotationPoint(0, 0, 2);
    }

    @Override
    public void render(T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        if (skipCloakOnce) {
            skipCloakOnce = false;
        } else {
            bipedCloak.render(f5);
        }

    }

    @Override
    public void setRotationAngles(T e, float f1, float f2, float f3, float f4, float f5, float f6) {
        super.setRotationAngles(e, f1, f2, f3, f4, f5, f6);
        bipedCloak.rotateAngleX = f2;
    }

    public void setSkipCloakOnce() {
        skipCloakOnce = true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        bipedCloak.showModel = visible;
    }
}
