package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.render.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class VampireTaskMasterRenderer extends MobRenderer<VampireTaskMasterEntity, VillagerModel<VampireTaskMasterEntity>> {
    private final static ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");
    private final static ResourceLocation vampireOverlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");
    private final static ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_task_master_overlay.png");

    public VampireTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VillagerModel<>(0F), 0.5F);
//        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new VampireEntityLayer<>(this, vampireOverlay, false));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
    }

    @Override
    public ResourceLocation getEntityTexture(VampireTaskMasterEntity entity) {
        return texture;
    }

    @Override
    protected void renderName(VampireTaskMasterEntity p_225629_1_, String p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        double dist = this.renderManager.squareDistanceTo(p_225629_1_);
        if (dist <= 128) {
            super.renderName(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
        }
    }


}
