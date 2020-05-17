package de.teamlapen.vampirism.client.render.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class PlayerBodyOverlayLayer<T extends MinionEntity & IPlayerOverlay, M extends MinionModel<T>> extends LayerRenderer<T,M> {
    public PlayerBodyOverlayLayer(IEntityRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation loc = getEntityTexture(entitylivingbaseIn);
        GameProfile prof = entitylivingbaseIn.getOverlayPlayerProfile();
        if (prof != null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(prof);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                loc = Minecraft.getInstance().getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }

        }
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(loc));
        this.getEntityModel().getBodyParts().forEach(
                b->b.showModel=true
        );
        this.getEntityModel().getBodyParts().forEach(b->b.render(matrixStackIn,vertexBuilder,packedLightIn,OverlayTexture.NO_OVERLAY,1,1,1,1));
        this.getEntityModel().getBodyParts().forEach(
                b->b.showModel=false
        );
    }
}
