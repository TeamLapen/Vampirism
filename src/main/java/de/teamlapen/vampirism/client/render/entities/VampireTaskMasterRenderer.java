package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class VampireTaskMasterRenderer extends BipedRenderer<VampireTaskMasterEntity, BipedModel<VampireTaskMasterEntity>> {
    private final ResourceLocation[] textures;

    public VampireTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0F,false), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/entity/vampire", s -> s.endsWith(".png")).stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    @Override
    protected ResourceLocation getEntityTexture(VampireTaskMasterEntity entity) {
        return getVampireTexture(entity.getEntityId());
    }

    @Override
    protected void renderLivingLabel(@Nonnull VampireTaskMasterEntity entityIn, @Nonnull String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }


}
