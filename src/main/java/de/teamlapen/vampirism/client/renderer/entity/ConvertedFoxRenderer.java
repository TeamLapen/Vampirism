package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.renderer.entity.layers.VampireEntityLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.resources.ResourceLocation;

public class ConvertedFoxRenderer extends FoxRenderer {
    public ConvertedFoxRenderer(EntityRendererProvider.Context p_174127_) {
        super(p_174127_);
        this.addLayer(new VampireEntityLayer<>(this, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/fox_overlay.png"), false));
    }
}
