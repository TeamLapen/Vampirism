package de.teamlapen.vampirism.client.render.entities;

import com.google.common.base.Predicates;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.client.render.LayerHunterEquipment;
import de.teamlapen.vampirism.client.render.LayerPlayerBodyOverlay;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class HunterMinionRenderer extends BipedRenderer<HunterMinionEntity, MinionModel<HunterMinionEntity>> {
    private final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base3.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base4.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base5.png")
    };

    public HunterMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MinionModel<>(0.5f, false), 0.5F);
        this.addLayer(new LayerPlayerBodyOverlay<>(this, HunterMinionEntity::shouldRenderLordSkin));
        this.addLayer(new LayerHunterEquipment<>(this, new HunterEquipmentModel.Minion(), Predicates.alwaysFalse(), HunterMinionEntity::getHatType));
    }


    @Override
    protected ResourceLocation getEntityTexture(HunterMinionEntity entity) {
        return textures[entity.getHunterType() % textures.length];
    }


}
