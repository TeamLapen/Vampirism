package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.render.LayerHunterEquipment;
import de.teamlapen.vampirism.entity.hunter.HunterTrainerEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterTrainerRenderer extends BipedRenderer<HunterTrainerEntity, PlayerModel<HunterTrainerEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_trainer.png");


    public HunterTrainerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0, false), 0.5F);
        this.addLayer(new LayerHunterEquipment<>(this, h -> HunterEquipmentModel.StakeType.ONLY, entityModel -> 1));
        //this.addLayer(new LayerCloak<>(this, texture, Predicates.alwaysTrue()));

    }

    @Override
    protected ResourceLocation getEntityTexture(HunterTrainerEntity entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(HunterTrainerEntity entityIn, String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 6);
    }

}
