package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Renders an overlay over the entities face
 */
public class PlayerFaceOverlayLayer<T extends Mob & IPlayerOverlay, M extends HumanoidModel<T>> extends RenderLayer<T, M> {


    public PlayerFaceOverlayLayer(@NotNull HumanoidMobRenderer<T, M> renderBiped) {
        super(renderBiped);
    }

    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation loc = entity.getPlayerOverlay().map(p -> Minecraft.getInstance().getSkinManager().getInsecureSkin(p)).map(PlayerSkin::texture).orElseGet(DefaultPlayerSkin::getDefaultTexture);
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(loc));
        this.getParentModel().head.visible = true;
        this.getParentModel().hat.visible = true;
        this.getParentModel().head.render(stack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY,-1);
        this.getParentModel().hat.render(stack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, -1);
        this.getParentModel().head.visible = false;
        this.getParentModel().hat.visible = false;

    }


}
