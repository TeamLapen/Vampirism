package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.MinionRenderState;
import de.teamlapen.vampirism.common.util.PlayerSkinHelper;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
public class HunterMinionRenderer extends DualBipedRenderer<HunterMinionEntity, HunterMinionRenderer.HunterMinionRenderState, PlayerBodyOverlayLayer.VisibilityPlayerModel<HunterMinionRenderer.HunterMinionRenderState>> {
    private final PlayerSkin @NotNull [] textures;
    private final PlayerSkin @NotNull [] minionSpecificTextures;


    public HunterMinionRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
        this.textures = gatherTextures("textures/entity/hunter", true);
        this.minionSpecificTextures = gatherTextures("textures/entity/minion/hunter", false);
        this.addLayer(new PlayerBodyOverlayLayer<>(this, new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true)));
        this.addLayer(new ArmorLayer<ClothedModel<HunterMinionRenderState>>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(x, true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(x, false)),
                context.getEquipmentRenderer()));
    }

    public int getHunterTextureCount() {
        return this.textures.length;
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    @Override
    protected PlayerSkin determineTextureAndModel(@NotNull HunterMinionRenderState entity) {
        return entity.skin;
    }

    @Override
    protected void submitNameTag(HunterMinionRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0, 0.2f, 0);
        super.submitNameTag(renderState, poseStack, nodeCollector, cameraRenderState);
        poseStack.popPose();
    }

    @Override
    public @NotNull HunterMinionRenderState createRenderState() {
        return new HunterMinionRenderState();
    }

    @Override
    public void extractRenderState(@NotNull HunterMinionEntity entity, @NotNull HunterMinionRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        if (entity.getMinionData().filter(HunterMinionEntity.HunterMinionData::isUsingLordSkin).isPresent()) {
            state.lordSkin = entity.getLordID().map(x -> Minecraft.getInstance().getConnection().getPlayerInfo(x)).map(PlayerInfo::getSkin).orElse(null);
        }
        state.skin = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getHunterType() % minionSpecificTextures.length] : textures[entity.getHunterType() % textures.length];
    }

    @Override
    protected HumanoidModel.@NotNull ArmPose getArmPose(HunterMinionEntity entity, @NotNull HumanoidArm arm) {
        var hand = arm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack itemstack = entity.getItemInHand(hand);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
                ItemUseAnimation useanim = itemstack.getUseAnimation();
                if (useanim == ItemUseAnimation.CROSSBOW && hand == entity.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else if (!entity.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            HumanoidModel.ArmPose forgeArmPose = IClientItemExtensions.of(itemstack).getArmPose(entity, hand, itemstack);
            if (forgeArmPose != null) return forgeArmPose;

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    public static class HunterMinionRenderState extends MinionRenderState {
    }
}