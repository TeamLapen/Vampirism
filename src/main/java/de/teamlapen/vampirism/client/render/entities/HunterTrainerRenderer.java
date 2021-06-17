package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterTrainerRenderer extends BipedRenderer<MobEntity, PlayerModel<MobEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_trainer.png");


    public HunterTrainerRenderer(EntityRendererManager renderManagerIn, boolean renderEquipment) {
        super(renderManagerIn, new PlayerModel<>(0,false), 0.5F);
        if(renderEquipment)this.addLayer(new HunterEquipmentLayer<>(this, h-> HunterEquipmentModel.StakeType.ONLY, entityModel -> 1));
        //this.addLayer(new CloakLayer<>(this, textureCloak, Predicates.alwaysTrue()));
    }


    @Override
    public ResourceLocation getEntityTexture(MobEntity entity) {
        return texture;
    }

    @Override
    protected void renderName(MobEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        double dist = this.renderManager.squareDistanceTo(entityIn);
        if (dist <= 128) {
            super.renderName(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }

}
