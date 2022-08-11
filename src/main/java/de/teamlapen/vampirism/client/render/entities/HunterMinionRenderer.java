package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.HunterMinionModel;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class HunterMinionRenderer extends DualBipedRenderer<HunterMinionEntity, HunterMinionModel<HunterMinionEntity>> {
    private final Pair<ResourceLocation, Boolean>[] textures;
    private final Pair<ResourceLocation, Boolean>[] minionSpecificTextures;


    public HunterMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HunterMinionModel<>(0.5f, false), new HunterMinionModel<>(0.5f, true), 0.5F);
        textures = gatherTextures("textures/entity/hunter", true);
        minionSpecificTextures = gatherTextures("textures/entity/minion/hunter", false);
        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new HunterEquipmentLayer<>(this, minion -> minion.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty() ? minion.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty() ? HunterEquipmentModel.StakeType.FULL : HunterEquipmentModel.StakeType.AXE_ONLY : HunterEquipmentModel.StakeType.NONE, HunterMinionEntity::getHatType));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5f), new BipedModel<>(1f)));
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
    protected void renderSelected(HunterMinionEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.renderSelected(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected void scale(HunterMinionEntity entityIn, MatrixStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

    @Override
    protected void renderNameTag(HunterMinionEntity p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        p_225629_3_.pushPose();
        p_225629_3_.translate(0,0.4f,0);
        super.renderNameTag(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
        p_225629_3_.popPose();
    }
}