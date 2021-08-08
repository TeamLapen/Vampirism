package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicVampireRenderer extends HumanoidMobRenderer<BasicVampireEntity, HumanoidModel<BasicVampireEntity>> {

    private final ResourceLocation[] textures;

    public BasicVampireRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0, false), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().listResources("textures/entity/vampire", s -> s.endsWith(".png")).stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
    }

    @Override
    public ResourceLocation getTextureLocation(BasicVampireEntity entity) {
        return getVampireTexture(entity.getEntityTextureType());
    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }
}
