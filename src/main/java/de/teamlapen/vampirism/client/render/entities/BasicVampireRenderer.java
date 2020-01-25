package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicVampireRenderer extends BipedRenderer<BasicVampireEntity, BipedModel<BasicVampireEntity>> {

    private static final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire1.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire3.png"),
    };

    public static ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    public BasicVampireRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BipedModel<>(RenderType::entityCutoutNoCull, 0F, 0F, 64, 64), 0.5F);
    }

    @Override
    public ResourceLocation getEntityTexture(BasicVampireEntity entity) {
        return getVampireTexture(entity.getEntityId());
    }
}
