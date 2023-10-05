package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.RemainsDefenderModel;
import de.teamlapen.vampirism.entity.RemainsDefenderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RemainsDefenderRenderer extends LivingEntityRenderer<RemainsDefenderEntity, RemainsDefenderModel> {
    private final ResourceLocation TEX = new ResourceLocation(REFERENCE.MODID, "textures/entity/remains_defender.png");

    public RemainsDefenderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new RemainsDefenderModel(pContext.bakeLayer(ModEntitiesRender.REMAINS_DEFENDER)), 0.1f);
    }

    @Override
    protected boolean shouldShowName(RemainsDefenderEntity pEntity) {
        return false;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RemainsDefenderEntity pEntity) {
        return TEX;
    }

    @Override
    protected void setupRotations(RemainsDefenderEntity pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        pMatrixStack.translate(0, 0.5d,0);
        pMatrixStack.mulPose(pEntityLiving.getAttachFace().getOpposite().getRotation());
        pMatrixStack.translate(0,-0.5,0);
    }
}
