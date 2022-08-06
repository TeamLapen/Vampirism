package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.render.layers.CloakLayer;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterRenderer extends DualBipedRenderer<BasicHunterEntity, BasicHunterModel<BasicHunterEntity>> {
    private final Pair<ResourceLocation, Boolean> textureDefault = Pair.of(new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png"),false);
    private final Pair<ResourceLocation, Boolean>[] textures;
    private final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_cloak.png");

    public BasicHunterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BasicHunterModel<>(false), new BasicHunterModel<>(true), 0.5F);
        this.addLayer(new HunterEquipmentLayer<>(this, entity -> (entity.getLevel() < 2 || entity.isHoldingCrossbow()) ? HunterEquipmentModel.StakeType.ONLY : HunterEquipmentModel.StakeType.FULL, entity -> entity.getLevel() == 0 ? entity.getEntityTextureType() % 4 : -1));
        this.addLayer(new CloakLayer<>(this, textureCloak, entity -> entity.getLevel() > 0));
        textures = gatherTextures("textures/entity/hunter", true);
    }


    @Override
    protected Pair<ResourceLocation, Boolean> determineTextureAndModel(BasicHunterEntity entity) {
        int level = entity.getLevel();
        if (level > 0) return textureDefault;
        return textures[entity.getEntityTextureType() % textures.length];
    }
}
