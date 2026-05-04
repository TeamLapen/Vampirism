package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.common.world.entity.hunter.BasicHunterEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
public class BasicHunterRenderer extends DualBipedRenderer<BasicHunterEntity, BasicHunterEntity.BasicHunterRenderState, ClothedModel<BasicHunterEntity.BasicHunterRenderState>> {

    private final @NotNull PlayerSkin[] textures;

    public BasicHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false), new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
        this.addLayer(new ArmorLayer<HumanoidModel<BasicHunterEntity.BasicHunterRenderState>>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, false)),
                context.getEquipmentRenderer()));
        this.textures = gatherTextures("textures/entity/hunter", true);
    }


    @Override
    public @NotNull BasicHunterEntity.BasicHunterRenderState createRenderState() {
        return new BasicHunterEntity.BasicHunterRenderState();
    }

    @Override
    public void extractRenderState(@NotNull BasicHunterEntity entity, BasicHunterEntity.@NotNull BasicHunterRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        state.skin = textures[entity.getEntityTextureType() % textures.length];
        state.entityLevel = entity.getEntityLevel();

    }
}
