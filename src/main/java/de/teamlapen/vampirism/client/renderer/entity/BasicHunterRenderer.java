package de.teamlapen.vampirism.client.renderer.entity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.renderer.entity.layers.CloakLayer;
import de.teamlapen.vampirism.client.renderer.entity.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterRenderer extends DualBipedRenderer<BasicHunterEntity, BasicHunterModel<BasicHunterEntity>> {

    private static final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_cloak.png");

    private final Pair<ResourceLocation, Boolean> textureDefault = Pair.of(new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png"), false);
    private final Pair<ResourceLocation, Boolean> @NotNull [] textures;

    public BasicHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER_SLIM), true), 0.5F);
        this.addLayer(new HunterEquipmentLayer<>(this, context.getModelSet(), entity -> entity.isHoldingCrossbow() ? HunterEquipmentModel.StakeType.NONE : entity.getEntityLevel() < 2 ? HunterEquipmentModel.StakeType.ONLY : HunterEquipmentModel.StakeType.FULL, entity -> entity.getEntityLevel() == 0 ? HunterEquipmentModel.HatType.from(entity.getEntityTextureType() % 3) : HunterEquipmentModel.HatType.HAT1));
        this.addLayer(new CloakLayer<>(this, textureCloak, entity -> entity.getEntityLevel() > 0));
        textures = gatherTextures("textures/entity/hunter", true);
    }


    @Override
    protected Pair<ResourceLocation, Boolean> determineTextureAndModel(@NotNull BasicHunterEntity entity) {
        int level = entity.getEntityLevel();
        if (level > 0) return textureDefault;
        return textures[entity.getEntityTextureType() % textures.length];
    }
}
