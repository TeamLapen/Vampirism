package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.VampirismBoatEntity;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class VampirismBoatRenderer extends BoatRenderer {
    private static final ResourceLocation[] BOAT_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation(REFERENCE.MODID,"textures/entity/boat/dark_spruce.png"), new ResourceLocation(REFERENCE.MODID, "textures/entity/boat/cursed_spruce.png")};

    public VampirismBoatRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull BoatEntity p_110775_1_) {
        return BOAT_TEXTURE_LOCATIONS[((VampirismBoatEntity) p_110775_1_).getBType().ordinal()];
    }
}
