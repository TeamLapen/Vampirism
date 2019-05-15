package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.util.REFERENCE;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBasicVampire extends RenderBiped<EntityBasicVampire> {

    private static final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire1.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire3.png"),
    };

    public static ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    public RenderBasicVampire(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBiped(0F, 0F, 64, 64), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBasicVampire entity) {
        return getVampireTexture(entity.getEntityId());
    }
}
