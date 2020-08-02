package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.render.LayerTaskMasterType;
import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
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
public class VampireTaskMasterRenderer extends MobRenderer<VampireTaskMasterEntity, VillagerModel<VampireTaskMasterEntity>> {

    private final static ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");
    private final static ResourceLocation vampireOverlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");
    private final static ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_task_master_overlay.png");


    public VampireTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VillagerModel<>(0f), 0.5F);
        this.addLayer(new VillagerHeldItemLayer<>(this));
        this.addLayer(new LayerVampireEntity<>(this, vampireOverlay, false));
        this.addLayer(new LayerTaskMasterType<>(this, overlay));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(VampireTaskMasterEntity entity) {
        return texture;
    }


    @Override
    protected void renderLivingLabel(@Nonnull VampireTaskMasterEntity entityIn, @Nonnull String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 6);
    }


}
