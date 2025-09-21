package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entities.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.MinionRenderState;
import de.teamlapen.vampirism.common.entity.minion.VampireMinionEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.PlayerSkin;
import org.jetbrains.annotations.NotNull;

public class VampireMinionRenderer extends DualBipedRenderer<VampireMinionEntity, VampireMinionRenderer.VampireMinionRenderState, PlayerBodyOverlayLayer.VisibilityPlayerModel<VampireMinionRenderer.VampireMinionRenderState>> {

    private final PlayerSkin @NotNull [] textures;
    private final PlayerSkin @NotNull [] minionSpecificTextures;


    public VampireMinionRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), new PlayerBodyOverlayLayer.VisibilityPlayerModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED_SLIM), true), 0.5F);
        this.textures = gatherTextures("textures/entity/vampire", true);
        this.minionSpecificTextures = gatherTextures("textures/entity/minion/vampire", false);

        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED_ARMOR_INNER)), new HumanoidModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED_ARMOR_OUTER)), context.getEquipmentRenderer()));
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
        state.skin = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getVampireType() % minionSpecificTextures.length] : textures[entity.getVampireType() % textures.length];
    }


    public static class VampireMinionRenderState extends MinionRenderState {
    }
}