package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelVillagerWithArms;
import de.teamlapen.vampirism.client.render.LayerHeldItemVillager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHunterVillager extends RenderVillager {
    public RenderHunterVillager(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.layerRenderers.clear();
        this.mainModel = new ModelVillagerWithArms(0.0F);
        this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead)); //TODO no visible
        this.addLayer(new LayerHeldItemVillager(this));
    }
}
