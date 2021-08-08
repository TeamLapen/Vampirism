package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class HunterMinionRenderer extends DualBipedRenderer<HunterMinionEntity, PlayerModel<HunterMinionEntity>> {
    private final Pair<ResourceLocation, Boolean>[] textures;
    private final Pair<ResourceLocation, Boolean>[] minionSpecificTextures;


    public HunterMinionRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0.5f, false), new PlayerModel<>(0.5f, true), 0.5F);
        textures = gatherTextures("textures/entity/hunter", true);
        minionSpecificTextures = gatherTextures("textures/entity/minion/hunter", false);
        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new HunterEquipmentLayer<>(this, minion -> minion.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() ? minion.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() ? HunterEquipmentModel.StakeType.FULL : HunterEquipmentModel.StakeType.AXE_ONLY : HunterEquipmentModel.StakeType.NONE, HunterMinionEntity::getHatType));
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(0.5f), new HumanoidModel<>(1f)));
    }

    public int getHunterTextureCount() {
        return this.textures.length;
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    @Override
    protected Pair<ResourceLocation, Boolean> determineTextureAndModel(HunterMinionEntity entity) {
        Pair<ResourceLocation, Boolean> p = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getHunterType() % minionSpecificTextures.length] : textures[entity.getHunterType() % textures.length];
        if (entity.shouldRenderLordSkin()) {
            return entity.getOverlayPlayerProperties().map(Pair::getRight).map(b -> Pair.of(p.getLeft(), b)).orElse(p);
        }
        return p;
    }

    @Override
    protected void renderSelected(HunterMinionEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.isSwingingArms()) {
            this.model.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
        } else {
            this.model.rightArmPose = HumanoidModel.ArmPose.ITEM;
        }
        super.renderSelected(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected void scale(HunterMinionEntity entityIn, PoseStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

}