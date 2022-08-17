package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class HunterTrainerRenderer extends HumanoidMobRenderer<Mob, PlayerModel<Mob>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_trainer.png");


    public HunterTrainerRenderer(EntityRendererProvider.@NotNull Context context, boolean renderEquipment) {
        super(context, new PlayerModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), 0.5F);
        if (renderEquipment)
            this.addLayer(new HunterEquipmentLayer<>(this, context.getModelSet(), h -> HunterEquipmentModel.StakeType.ONLY, entityModel -> HunterEquipmentModel.HatType.HAT2));
        //this.addLayer(new CloakLayer<>(this, textureCloak, Predicates.alwaysTrue()));
    }


    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull Mob entity) {
        return texture;
    }

    @Override
    protected void renderNameTag(@NotNull Mob entityIn, @NotNull Component displayNameIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 128) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }

}
