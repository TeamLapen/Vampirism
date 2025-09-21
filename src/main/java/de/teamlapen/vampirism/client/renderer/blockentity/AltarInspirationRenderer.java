package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.client.renderer.VertexUtils;
import de.teamlapen.vampirism.common.blockentity.AltarInspirationBlockEntity;
import de.teamlapen.vampirism.common.core.ModFluids;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class AltarInspirationRenderer implements BlockEntityRenderer<AltarInspirationBlockEntity> {

    public AltarInspirationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AltarInspirationBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexUtils.renderFluidTank(
                ModFluids.BLOOD,
                blockEntity.getModelData().get(AltarInspirationBlockEntity.FLUID_AMOUNT),
                AltarInspirationBlockEntity.CAPACITY,
                new Vec3(8 / 16f, 1 / 16f, 8 / 16f),
                new Vec3(8 / 16f,10.8 / 16f,8 / 16f),
                0.85f,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );
    }
}
