package de.teamlapen.vampirism.client.render.entities;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.render.layers.CloakLayer;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedHunterRenderer extends BipedRenderer<AdvancedHunterEntity, BasicHunterModel<AdvancedHunterEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png");
    private static final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire_baron_cloak.png");


    public AdvancedHunterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BasicHunterModel<>(), 0.5F);
        this.addLayer(new HunterEquipmentLayer<>(this, Predicates.alwaysFalse(), AdvancedHunterEntity::getHunterType));
        this.addLayer(new CloakLayer<>(this, textureCloak, Predicates.alwaysTrue()));
        if (VampirismConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.getEntityModel().bipedHead.showModel = false;
            this.getEntityModel().bipedHeadwear.showModel = false;
        }
    }

    @Override
    public ResourceLocation getEntityTexture(AdvancedHunterEntity entity) {
        return texture;
    }

    @Override
    protected void renderName(AdvancedHunterEntity p_225629_1_, String p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        double dist = this.renderManager.squareDistanceTo(p_225629_1_);
        if (dist <= 256) {
            super.renderName(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
        }
    }

}
