package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.minion.VampireMinion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 1.14
 *
 * @author maxanier
 */
@OnlyIn(Dist.CLIENT)
public class VampireMinionRenderer extends BipedRenderer<VampireMinion, BipedModel<VampireMinion>> {

    private final ResourceLocation[] textures;

    public VampireMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0F, false), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/entity/vampire", s -> s.endsWith(".png")).stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    @Override
    public ResourceLocation getEntityTexture(VampireMinion entity) {
        return getVampireTexture(entity.getEntityId());
    }
}
