package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.client.render.LayerPlayerBodyOverlay;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireMinionRenderer extends BipedRenderer<VampireMinionEntity, MinionModel<VampireMinionEntity>> {

    private final ResourceLocation[] textures;

    public VampireMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MinionModel<>(0F, false), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/entity/vampire", s -> s.endsWith(".png")).stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
        this.addLayer(new LayerPlayerBodyOverlay<>(this));
    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    @Override
    protected ResourceLocation getEntityTexture(VampireMinionEntity entity) {
        return getVampireTexture(entity.getEntityId());
    }
}
