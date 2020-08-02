package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.render.LayerHunterEquipment;
import de.teamlapen.vampirism.client.render.LayerTaskMasterType;
import de.teamlapen.vampirism.entity.hunter.HunterTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.VillagerHeldItemLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class HunterTaskMasterRenderer extends MobRenderer<HunterTaskMasterEntity, VillagerModel<HunterTaskMasterEntity>> {

    private final static ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");
    private final static ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_task_master_overlay.png");


    public HunterTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VillagerModel<>(0f), 0.5F);
        this.addLayer(new VillagerHeldItemLayer<>(this));
        this.addLayer(new LayerTaskMasterType<>(this, overlay));
        this.addLayer(new LayerHunterEquipment<>(this, h -> HunterEquipmentModel.StakeType.NONE, h -> 1));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(HunterTaskMasterEntity entity) {
        return texture;
    }


    @Override
    protected void renderLivingLabel(@Nonnull HunterTaskMasterEntity entityIn, @Nonnull String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 6);
    }


}
