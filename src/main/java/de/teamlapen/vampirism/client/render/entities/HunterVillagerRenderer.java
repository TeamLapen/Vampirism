package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.VillagerWithArmsModel;
import de.teamlapen.vampirism.client.render.LayerHeldItemVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterVillagerRenderer extends VillagerRenderer {
    public HunterVillagerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, (IReloadableResourceManager) Minecraft.getInstance().getResourceManager());
        this.layerRenderers.clear();
        this.entityModel = new VillagerWithArmsModel(0.0F);
        this.addLayer(new LayerHeldItemVillager(this));
    }
}
