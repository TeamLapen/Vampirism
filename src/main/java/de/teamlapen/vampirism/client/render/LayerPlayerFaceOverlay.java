package de.teamlapen.vampirism.client.render;

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
public class LayerPlayerFaceOverlay<T extends MobEntity & IPlayerFace, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    private final BipedRenderer<T, M> renderBiped;

    public LayerPlayerFaceOverlay(BipedRenderer<T, M> renderBiped) {
        super(renderBiped);
        this.renderBiped = renderBiped;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, T entityIn, float v, float v1, float v2, float v3, float v4, float v5) {
        ResourceLocation loc = DefaultPlayerSkin.getDefaultSkinLegacy();
        GameProfile prof = entityIn.getPlayerFaceProfile();
        if (prof != null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(prof);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                loc = Minecraft.getInstance().getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }

        }
        IVertexBuilder vertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.entitySolid(loc));
        this.getEntityModel().bipedHead.render(matrixStack, vertexBuilder, i, 0, v, v1, v2, v3);
        this.getEntityModel().bipedHeadwear.render(matrixStack, vertexBuilder, i, 0, v, v1, v2, v3);

    }


}
