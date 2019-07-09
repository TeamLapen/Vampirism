package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelVillagerWithArms;
import de.teamlapen.vampirism.client.render.LayerHeldItemVillager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHunterVillager extends VillagerRenderer {
    public RenderHunterVillager(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.layerRenderers.clear();
        this.mainModel = new ModelVillagerWithArms(0.0F);
        this.addLayer(new HeadLayer(this.getMainModel().villagerHead));
        this.addLayer(new LayerHeldItemVillager(this));
    }
}
