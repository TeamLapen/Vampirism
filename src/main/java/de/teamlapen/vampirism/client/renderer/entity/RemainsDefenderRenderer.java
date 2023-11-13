package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.RemainsDefenderModel;
import de.teamlapen.vampirism.entity.RemainsDefenderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RemainsDefenderRenderer extends MobRenderer<RemainsDefenderEntity, RemainsDefenderModel> {
    private final ResourceLocation TEX1 = new ResourceLocation(REFERENCE.MODID, "textures/entity/remains_defender/remains_defender1.png");
    private final ResourceLocation TEX2 = new ResourceLocation(REFERENCE.MODID, "textures/entity/remains_defender/remains_defender2.png");
    private final ResourceLocation TEX3 = new ResourceLocation(REFERENCE.MODID, "textures/entity/remains_defender/remains_defender3.png");
    private final ResourceLocation TEX4 = new ResourceLocation(REFERENCE.MODID, "textures/entity/remains_defender/remains_defender4.png");

    public RemainsDefenderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new RemainsDefenderModel(pContext.bakeLayer(ModEntitiesRender.REMAINS_DEFENDER)), 0f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RemainsDefenderEntity pEntity) {
        int t = pEntity.tickCount % 20;
        if (t> 15) {
            return TEX4;
        } else if (t > 10) {
            return TEX3;
        } else if (t > 5) {
            return TEX2;
        } else {
            return TEX1;
        }
    }

    @Override
    protected void setupRotations(RemainsDefenderEntity pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        pMatrixStack.translate(0, 0.5d,0);
        pMatrixStack.mulPose(pEntityLiving.getAttachFace().getOpposite().getRotation());
        pMatrixStack.translate(0,-0.5,0);
    }

    @Override
    protected int getBlockLightLevel(RemainsDefenderEntity pEntity, BlockPos pPos) {
        int i = (int) Mth.clampedLerp(0.0F, 15.0F, (float)pEntity.getLightTicksRemaining() / 10.0F);
        return i == 15 ? 15 : Math.max(i, super.getBlockLightLevel(pEntity, pPos));
    }
}
