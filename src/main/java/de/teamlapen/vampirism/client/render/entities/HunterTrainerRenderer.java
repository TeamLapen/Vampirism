package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterTrainerRenderer extends HumanoidMobRenderer<Mob, PlayerModel<Mob>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_trainer.png");


    public HunterTrainerRenderer(EntityRenderDispatcher renderManagerIn, boolean renderEquipment) {
        super(renderManagerIn, new PlayerModel<>(0, false), 0.5F);
        if (renderEquipment)
            this.addLayer(new HunterEquipmentLayer<>(this, h -> HunterEquipmentModel.StakeType.ONLY, entityModel -> 1));
        //this.addLayer(new CloakLayer<>(this, textureCloak, Predicates.alwaysTrue()));
    }


    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(Mob entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 128) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }

}
