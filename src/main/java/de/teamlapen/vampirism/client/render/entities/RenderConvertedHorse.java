package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderConvertedHorse extends RenderHorse {

    private final ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png");

    public RenderConvertedHorse(RenderManager renderManager) {
        super(renderManager);
        this.addLayer(new LayerVampireEntity(this, overlay, false));
    }
}
