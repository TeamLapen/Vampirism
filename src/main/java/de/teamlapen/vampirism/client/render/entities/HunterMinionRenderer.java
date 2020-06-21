package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.client.render.LayerHunterEquipment;
import de.teamlapen.vampirism.client.render.LayerPlayerBodyOverlay;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class HunterMinionRenderer extends BipedRenderer<HunterMinionEntity, MinionModel<HunterMinionEntity>> {
    private final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base3.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base4.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base5.png")
    };

    public HunterMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MinionModel<>(0.5f), 0.5F);
        this.addLayer(new LayerPlayerBodyOverlay<>(this, HunterMinionEntity::shouldRenderLordSkin));
        this.addLayer(new LayerHunterEquipment<>(this, new HunterEquipmentModel<>(), hunterMinionEntity -> hunterMinionEntity.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() ? HunterEquipmentModel.StakeType.FULL : HunterEquipmentModel.StakeType.NONE, HunterMinionEntity::getHatType));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5f), new BipedModel<>(1f)));
    }


    @Override
    protected ResourceLocation getEntityTexture(HunterMinionEntity entity) {
        return textures[entity.getHunterType() % textures.length];
    }

    @Override
    protected void renderLayers(HunterMinionEntity entity, float p_177093_2_, float p_177093_3_, float p_177093_4_, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_) {
        //Scale layers
        float s = entity.getScale();
        float off = (1 - s) * 1.95f;
        GlStateManager.pushMatrix();
        GlStateManager.scalef(s, s, s);
        GlStateManager.translatef(0.0F, off, 0.0F);
        super.renderLayers(entity, p_177093_2_, p_177093_3_, p_177093_4_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
        GlStateManager.popMatrix();
    }


}
