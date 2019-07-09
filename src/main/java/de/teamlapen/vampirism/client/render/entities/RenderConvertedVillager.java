package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class RenderConvertedVillager extends VillagerRenderer {

    private final ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");

    public RenderConvertedVillager(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.addLayer(new LayerVampireEntity(this, overlay, false));
    }
}
