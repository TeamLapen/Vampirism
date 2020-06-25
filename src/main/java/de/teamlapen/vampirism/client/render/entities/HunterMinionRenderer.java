package de.teamlapen.vampirism.client.render.entities;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
    private final ResourceLocation[] textures;

    public HunterMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MinionModel<>(0.5f), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/entity/hunter", s -> s.endsWith(".png")).stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new HunterEquipmentLayer<>(this, hunterMinionEntity -> hunterMinionEntity.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() ? HunterEquipmentModel.StakeType.FULL : HunterEquipmentModel.StakeType.NONE, HunterMinionEntity::getHatType));
        this.addLayer(new BipedArmorLayer<>(this,new BipedModel<>(0.5f),new BipedModel<>(1f)));
    }

    @Override
    protected void preRenderCallback(HunterMinionEntity entityIn, MatrixStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s,s,s);
        //matrixStackIn.translate(0,off,0f);
    }

    @Override
    public void render(HunterMinionEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.isSwingingArms()) {
            this.entityModel.rightArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
        } else {
            this.entityModel.rightArmPose = BipedModel.ArmPose.ITEM;
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(HunterMinionEntity entity) {
        return textures[entity.getHunterType() % textures.length];
    }

    public int getTextureLength() {
        return this.textures.length;
    }

}