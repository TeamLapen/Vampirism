package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.VertexUtils;
import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.core.ModFluids;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class BloodContainerRenderer implements BlockEntityRenderer<BloodContainerBlockEntity> {

    public BloodContainerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BloodContainerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexUtils.renderFluidTank(
                ModFluids.BLOOD,
                blockEntity.getModelData().get(BloodContainerBlockEntity.FLUID_AMOUNT),
                BloodContainerBlockEntity.CAPACITY,
                new Vec3(8 / 16f, 1 / 16f, 8 / 16f),
                new Vec3(10 / 16f,13.8 / 16f,10 / 16f),
                0.85f,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );
    }
}
