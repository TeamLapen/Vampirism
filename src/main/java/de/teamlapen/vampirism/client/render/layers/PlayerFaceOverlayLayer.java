package de.teamlapen.vampirism.client.render.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.util.IPlayerFace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * Renders an overlay over the entities face
 */
@OnlyIn(Dist.CLIENT)
public class PlayerFaceOverlayLayer<T extends MobEntity & IPlayerFace, M extends BipedModel<T>> extends LayerRenderer<T, M> {


    public PlayerFaceOverlayLayer(BipedRenderer<T, M> renderBiped) {
        super(renderBiped);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation loc = DefaultPlayerSkin.getDefaultSkinLegacy();
        GameProfile prof = entityIn.getPlayerFaceProfile();
        if (prof != null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(prof);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                loc = Minecraft.getInstance().getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }

        }
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(loc));
        this.getEntityModel().bipedHead.showModel = true;
        this.getEntityModel().bipedHeadwear.showModel = true;
        this.getEntityModel().bipedHead.render(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        this.getEntityModel().bipedHeadwear.render(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        this.getEntityModel().bipedHead.showModel = false;
        this.getEntityModel().bipedHeadwear.showModel = false;

    }


}
