package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.state.MinionRenderState;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
public class HunterMinionRenderer extends DualSplitBipedRenderer<HunterMinionEntity, MinionRenderState, ClothedModel<MinionRenderState>> {
    private final PlayerSkin[] textures;
    private final PlayerSkin[] minionSpecificTextures;


    public HunterMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.PLAYER, ModelLayers.PLAYER_SLIM, ClothedModel::new, 0.5F);
        this.textures = gatherTextures("textures/entity/hunter", true);
        this.minionSpecificTextures = gatherTextures("textures/entity/minion/hunter", false);
        this.addLayer(new ArmorLayer<ClothedModel<MinionRenderState>>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, false)),
                context.getEquipmentRenderer()));
    }

    @Override
    protected boolean splitRenderingEnabled() {
        return true;
    }

    @Override
    protected Identifier getTexture(MinionRenderState renderState, RenderPart part) {
        if (part == RenderPart.BODY && renderState.lordSkin != null) {
            return renderState.lordSkin.body().texturePath();
        }
        return renderState.skin.body().texturePath();
    }

    @Override
    protected ClothedModel<MinionRenderState> provideBodyModel(MinionRenderState renderState) {
        if (renderState.lordSkin != null) {
            return switch (renderState.lordSkin.model()) {
                case WIDE -> this.wideModel;
                case SLIM -> this.tallModel;
            };
        }
        return super.provideBodyModel(renderState);
    }

    public int getHunterTextureCount() {
        return this.textures.length;
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }


    @Override
    protected void submitNameDisplay(MinionRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, 0.2f, 0);
        super.submitNameDisplay(state, poseStack, submitNodeCollector, camera);
        poseStack.popPose();
    }

    @Override
    public MinionRenderState createRenderState() {
        return new MinionRenderState();
    }

    @Override
    public void extractRenderState(HunterMinionEntity entity, MinionRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        if (entity.getMinionData().filter(HunterMinionEntity.HunterMinionData::isUsingLordSkin).isPresent()) {
            state.lordSkin = entity.getLordID().map(x -> Minecraft.getInstance().getConnection().getPlayerInfo(x)).map(PlayerInfo::getSkin).orElse(null);
        }
        state.skin = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getHunterType() % minionSpecificTextures.length] : textures[entity.getHunterType() % textures.length];
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(HunterMinionEntity entity, HumanoidArm arm) {
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
}