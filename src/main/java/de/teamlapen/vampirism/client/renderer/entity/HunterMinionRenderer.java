package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.HunterMinionModel;
import de.teamlapen.vampirism.client.renderer.entity.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
public class HunterMinionRenderer extends DualBipedRenderer<HunterMinionEntity, HunterMinionModel<HunterMinionEntity>> {
    private final PlayerSkin @NotNull [] textures;
    private final PlayerSkin @NotNull [] minionSpecificTextures;


    public HunterMinionRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new HunterMinionModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), new HunterMinionModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED_SLIM), true), 0.5F);
        this.textures = gatherTextures("textures/entity/hunter", true);
        this.minionSpecificTextures = gatherTextures("textures/entity/minion/hunter", false);
        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new ArmorLayer<HumanoidModel<HunterMinionEntity>>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    public int getHunterTextureCount() {
        return this.textures.length;
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    @Override
    protected PlayerSkin determineTextureAndModel(@NotNull HunterMinionEntity entity) {
        return (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getHunterType() % minionSpecificTextures.length] : textures[entity.getHunterType() % textures.length];
    }

    @Override
    protected void scale(@NotNull HunterMinionEntity entityIn, @NotNull PoseStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

    @Override
    protected void renderNameTag(@NotNull HunterMinionEntity pEntity, @NotNull Component pDisplayName, @NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, float partialTicks) {
        pMatrixStack.pushPose();
        pMatrixStack.translate(0, 0.4f, 0);
        super.renderNameTag(pEntity, pDisplayName, pMatrixStack, pBuffer, pPackedLight, partialTicks);
        pMatrixStack.popPose();
    }
}