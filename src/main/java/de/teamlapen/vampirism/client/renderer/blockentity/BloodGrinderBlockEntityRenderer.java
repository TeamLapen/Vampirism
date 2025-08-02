package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.VertexUtils;
import de.teamlapen.vampirism.blockentity.BloodGrinderBlockEntity;
import de.teamlapen.vampirism.core.ModFluids;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class BloodGrinderBlockEntityRenderer implements BlockEntityRenderer<BloodGrinderBlockEntity> {

    public BloodGrinderBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BloodGrinderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexUtils.renderFluidTank(
                ModFluids.BLOOD,
                blockEntity.getModelData().get(BloodGrinderBlockEntity.FLUID_AMOUNT),
                BloodGrinderBlockEntity.CAPACITY,
                new Vec3(8 / 16f, 4 / 16f, 8 / 16f),
                new Vec3(15.75f / 16f, 8 / 16f, 15.75f / 16f),
                0.85f,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );
    }
}
