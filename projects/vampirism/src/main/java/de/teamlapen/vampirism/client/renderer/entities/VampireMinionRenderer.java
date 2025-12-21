package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.client.renderer.entities.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.MinionRenderState;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;

public class VampireMinionRenderer extends DualBipedRenderer<VampireMinionEntity, VampireMinionRenderer.VampireMinionRenderState, PlayerBodyOverlayLayer.VisibilityPlayerModel<VampireMinionRenderer.VampireMinionRenderState>> {

    private final PlayerSkin @NotNull [] textures;
    private final PlayerSkin @NotNull [] minionSpecificTextures;


    public VampireMinionRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
        this.textures = gatherTextures("textures/entity/vampire", true);
        this.minionSpecificTextures = gatherTextures("textures/entity/minion/vampire", false);

        this.addLayer(new PlayerBodyOverlayLayer<>(this, new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true)));
        this.addLayer(new HumanoidArmorLayer<>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(x,true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x  -> new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(x,false)),
                context.getEquipmentRenderer()));
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    public int getVampireTextureCount() {
        return this.textures.length;
    }

    @Override
    public @NotNull VampireMinionRenderState createRenderState() {
        return new VampireMinionRenderState();
    }

    @Override
    protected PlayerSkin determineTextureAndModel(@NotNull VampireMinionRenderState state) {
        return state.skin;
    }

    @Override
    public void extractRenderState(VampireMinionEntity entity, VampireMinionRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        if (entity.getMinionData().filter(VampireMinionEntity.VampireMinionData::isUsingLordSkin).isPresent()) {
            state.lordSkin = entity.getLordID().map(x -> Minecraft.getInstance().getConnection().getPlayerInfo(x)).map(PlayerInfo::getSkin).orElse(null);
        }
        state.skin = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getVampireType() % minionSpecificTextures.length] : textures[entity.getVampireType() % textures.length];
    }


    public static class VampireMinionRenderState extends MinionRenderState {
    }
}