package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.renderer.entity.layers.CloakLayer;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.util.PlayerModelType;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
public class BasicHunterRenderer extends DualBipedRenderer<BasicHunterEntity, BasicHunterModel<BasicHunterEntity>> {

    private static final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_cloak.png");

    private final Pair<ResourceLocation, PlayerModelType> @NotNull [] textures;

    public BasicHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER_SLIM), true), 0.5F);
        this.addLayer(new ArmorLayer<HumanoidModel<BasicHunterEntity>>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new CloakLayer<>(this, textureCloak, entity -> entity.getEntityLevel() > 0));
        textures = gatherTextures("textures/entity/hunter", true);
    }


    @Override
    protected Pair<ResourceLocation, PlayerModelType> determineTextureAndModel(@NotNull BasicHunterEntity entity) {
        return textures[entity.getEntityTextureType() % textures.length];
    }
}
