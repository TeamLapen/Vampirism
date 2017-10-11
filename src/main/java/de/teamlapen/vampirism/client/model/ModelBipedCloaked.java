package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ModelBiped with a cloak
 */
@SideOnly(Side.CLIENT)
public class ModelBipedCloaked extends ModelBiped {
    protected ModelRenderer bipedCloak;
    private boolean skipCloakOnce = false;

    public ModelBipedCloaked(float f1, float f2, int texWidth, int texHeight) {
        this(f1, f2, texWidth, texHeight, 65, 0);
    }

    public ModelBipedCloaked(float f1, float f2, int texWidth, int texHeight, int capeX, int capeY) {
        super(f1, f2, texWidth, texHeight);
        bipedCloak = new ModelRenderer(this, capeX, capeY);
        bipedCloak.addBox(-7.0F, 0.0F, 0.4F, 14, 20, 1);
        bipedCloak.setRotationPoint(0, 0, 2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        if (skipCloakOnce) {
            skipCloakOnce = false;
        } else {
            bipedCloak.render(f5);
        }

    }

    @Override
    public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6, Entity e) {
        super.setRotationAngles(f1, f2, f3, f4, f5, f6, e);
        bipedCloak.rotateAngleX = f2;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        bipedCloak.showModel = visible;
    }

    public void setSkipCloakOnce() {
        skipCloakOnce = true;
    }
}
