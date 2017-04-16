package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderConvertedVillager extends RenderVillager {

    private final ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");

    public RenderConvertedVillager(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.addLayer(new LayerVampireEntity(this, overlay, false));
    }
}
