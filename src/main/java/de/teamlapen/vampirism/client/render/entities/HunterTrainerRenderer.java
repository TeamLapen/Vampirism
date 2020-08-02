package de.teamlapen.vampirism.client.render.entities;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.render.layers.CloakLayer;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterTrainerRenderer extends BipedRenderer<MobEntity, PlayerModel<MobEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png");
    private static final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire_baron_cloak.png");


    public HunterTrainerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0,false), 0.5F);
        this.addLayer(new HunterEquipmentLayer<>(this, h-> HunterEquipmentModel.StakeType.ONLY, entityModel -> 1));
        //this.addLayer(new CloakLayer<>(this, textureCloak, Predicates.alwaysTrue()));
    }


    @Override
    public ResourceLocation getEntityTexture(MobEntity entity) {
        return texture;
    }

    @Override
    protected void renderName(MobEntity p_225629_1_, String p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        double dist = this.renderManager.squareDistanceTo(p_225629_1_);
        if (dist <= 128) {
            super.renderName(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
        }
    }

}
