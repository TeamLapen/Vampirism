package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.BasicHunterModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.CloakLayer;
import de.teamlapen.vampirism.common.entity.hunter.BasicHunterEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
public class BasicHunterRenderer extends DualBipedRenderer<BasicHunterEntity, BasicHunterEntity.BasicHunterRenderState, BasicHunterModel<BasicHunterEntity.BasicHunterRenderState>> {

    private static final ResourceLocation textureCloak = VResourceLocation.mod("textures/entity/hunter_cloak.png");

    private final @NotNull PlayerSkin[] textures;

    public BasicHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER_SLIM), true), 0.5F);
        this.addLayer(new ArmorLayer<HumanoidModel<BasicHunterEntity.BasicHunterRenderState>>(this,
                ArmorModelSet.bake(ModEntitiesRender.HUNTER_ARMOR_SLIM, context.getModelSet(), x -> new BasicHunterModel<>(x, true)),
                ArmorModelSet.bake(ModEntitiesRender.HUNTER_ARMOR, context.getModelSet(), x -> new BasicHunterModel<>(x, false)),
                context.getEquipmentRenderer()));
        this.addLayer(new CloakLayer<>(this, textureCloak, entity -> entity.entityLevel > 0));
        this.textures = gatherTextures("textures/entity/hunter", true);
    }


    @Override
    protected PlayerSkin determineTextureAndModel(@NotNull BasicHunterEntity.BasicHunterRenderState entity) {
        return entity.skin;
    }

    @Override
    public @NotNull BasicHunterEntity.BasicHunterRenderState createRenderState() {
        return new BasicHunterEntity.BasicHunterRenderState();
    }

    @Override
    public void extractRenderState(BasicHunterEntity entity, BasicHunterEntity.BasicHunterRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        state.skin = textures[entity.getEntityTextureType() % textures.length];
        state.entityLevel = entity.getEntityLevel();

    }
}
