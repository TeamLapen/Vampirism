package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelBipedCloaked;
import de.teamlapen.vampirism.client.render.LayerGlowingEyes;
import de.teamlapen.vampirism.entity.special.EntityDraculaHalloween;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderSpecialDraculaHalloween extends RenderLiving<EntityDraculaHalloween> {

    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/dracula.png");

    public RenderSpecialDraculaHalloween(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBipedCloaked(0, 0, 128, 64), 0.3F);
        this.addLayer(new LayerGlowingEyes<>(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/dracula_eyes.png")).setBrightness(160f));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityDraculaHalloween entity) {
        return texture;
    }
}
