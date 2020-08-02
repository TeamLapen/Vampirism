package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.VillagerWithArmsModel;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.entity.hunter.HunterTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class HunterTaskMasterRenderer extends MobRenderer<HunterTaskMasterEntity, VillagerWithArmsModel<HunterTaskMasterEntity>> {
    private final static ResourceLocation texture = new ResourceLocation("textures/entity/villager/villager.png");
    private final static ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_task_master_overlay.png");

    public HunterTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VillagerWithArmsModel<>(0f), 0.5F);
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
        this.addLayer(new HunterEquipmentLayer<>(this, h -> HunterEquipmentModel.StakeType.NONE, h -> 1));
    }

    @Override
    public ResourceLocation getEntityTexture(@Nonnull HunterTaskMasterEntity entity) {
        return texture;
    }

    @Override
    protected void renderName(HunterTaskMasterEntity p_225629_1_, String p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        double dist = this.renderManager.squareDistanceTo(p_225629_1_);
        if (dist <= 128) {
            super.renderName(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
        }
    }


}