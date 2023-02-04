package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.renderer.entity.layers.VampireEntityLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.minecraft.resources.ResourceLocation;

public class ConvertedGoatRenderer extends GoatRenderer {
    public ConvertedGoatRenderer(EntityRendererProvider.Context p_174153_) {
        super(p_174153_);
        this.addLayer(new VampireEntityLayer<>(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/goat_overlay.png"), false));
    }
}
