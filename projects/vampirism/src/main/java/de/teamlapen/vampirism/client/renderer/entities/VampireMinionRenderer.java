package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.state.MinionRenderState;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

public class VampireMinionRenderer extends DualSplitBipedRenderer<VampireMinionEntity, MinionRenderState, ClothedModel<MinionRenderState>> {

    private final PlayerSkin[] textures;
    private final PlayerSkin[] minionSpecificTextures;


    public VampireMinionRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.PLAYER, ModelLayers.PLAYER_SLIM, ClothedModel::new, 0.5F);
        this.textures = gatherTextures("textures/entity/vampire", true);
        this.minionSpecificTextures = gatherTextures("textures/entity/minion/vampire", false);
        this.addLayer(new HumanoidArmorLayer<>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x,true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x  -> new ClothedModel<>(x,false)),
                context.getEquipmentRenderer()));
    }

    @Override
    protected boolean splitRenderingEnabled() {
        return true;
    }

    @Override
    protected Identifier getTexture(MinionRenderState renderState, DualSplitBipedRenderer.RenderPart part) {
        if (part == DualSplitBipedRenderer.RenderPart.BODY && renderState.lordSkin != null) {
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

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    public int getVampireTextureCount() {
        return this.textures.length;
    }

    @Override
    public MinionRenderState createRenderState() {
        return new MinionRenderState();
    }

    @Override
    public void extractRenderState(VampireMinionEntity entity, MinionRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        if (entity.getMinionData().filter(VampireMinionEntity.VampireMinionData::isUsingLordSkin).isPresent()) {
            state.lordSkin = entity.getLordID().map(x -> Minecraft.getInstance().getConnection().getPlayerInfo(x)).map(PlayerInfo::getSkin).orElse(null);
        }
        state.skin = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getVampireType() % minionSpecificTextures.length] : textures[entity.getVampireType() % textures.length];
    }

}