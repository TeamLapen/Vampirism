package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.render.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class VampireMinionRenderer extends DualBipedRenderer<VampireMinionEntity, PlayerModel<VampireMinionEntity>> {

    private final Pair<ResourceLocation, Boolean>[] textures;
    private final Pair<ResourceLocation, Boolean>[] minionSpecificTextures;


    public VampireMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0F, false), new PlayerModel<>(0f, true), 0.5F);
        IResourceManager rm = Minecraft.getInstance().getResourceManager();
        textures = gatherTextures("textures/entity/vampire",true);
        minionSpecificTextures = gatherTextures("textures/entity/minion/vampire", false);

        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5f), new BipedModel<>(1f)));
        this.getEntityModel().bipedBody.showModel = this.getEntityModel().bipedBodyWear.showModel = false;
        this.getEntityModel().bipedLeftArm.showModel = this.getEntityModel().bipedLeftArmwear.showModel = this.getEntityModel().bipedRightArm.showModel = this.getEntityModel().bipedRightArmwear.showModel = false;
        this.getEntityModel().bipedRightLeg.showModel = this.getEntityModel().bipedRightLegwear.showModel = this.getEntityModel().bipedLeftLeg.showModel = this.getEntityModel().bipedLeftLegwear.showModel = false;
    }

    public int getMinionSpecificTextureCount() {
        return this.minionSpecificTextures.length;
    }

    public int getVampireTextureCount() {
        return this.textures.length;
    }

    @Override
    protected Pair<ResourceLocation, Boolean> determineTextureAndModel(VampireMinionEntity entity) {
        Pair<ResourceLocation, Boolean> p = (entity.hasMinionSpecificSkin() && this.minionSpecificTextures.length > 0) ? minionSpecificTextures[entity.getVampireType() % minionSpecificTextures.length] : textures[entity.getVampireType() % textures.length];
        if (entity.shouldRenderLordSkin()) {
            return entity.getOverlayPlayerProperties().map(Pair::getRight).map(b -> Pair.of(p.getLeft(), b)).orElse(p);
        }
        return p;
    }

    @Override
    protected void preRenderCallback(VampireMinionEntity entityIn, MatrixStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

}