package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.client.render.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class VampireMinionRenderer extends BipedRenderer<VampireMinionEntity, MinionModel<VampireMinionEntity>> {

    private final ResourceLocation[] textures;

    public VampireMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MinionModel<>(0F), 0.5F);
        IResourceManager rm = Minecraft.getInstance().getResourceManager();
        Collection<ResourceLocation> allTexs= new ArrayList<>(rm.getAllResourceLocations("textures/entity/vampire", s -> s.endsWith(".png")));
        allTexs.addAll(rm.getAllResourceLocations("textures/entity/minion/vampire", s -> s.endsWith(".png")));
        textures = allTexs.stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5f), new BipedModel<>(1f)));
        this.getEntityModel().bipedBody.showModel = this.getEntityModel().bipedBodyWear.showModel = false;
        this.getEntityModel().bipedLeftArm.showModel = this.getEntityModel().bipedLeftArmwear.showModel = this.getEntityModel().bipedRightArm.showModel = this.getEntityModel().bipedRightArmwear.showModel = false;
        this.getEntityModel().bipedRightLeg.showModel = this.getEntityModel().bipedRightLegwear.showModel = this.getEntityModel().bipedLeftLeg.showModel = this.getEntityModel().bipedLeftLegwear.showModel = false;
    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    @Override
    public ResourceLocation getEntityTexture(VampireMinionEntity entity) {
        return getVampireTexture(entity.getVampireType());
    }

    @Override
    protected void preRenderCallback(VampireMinionEntity entityIn, MatrixStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

    public int getTextureLength() {
        return this.textures.length;
    }
}