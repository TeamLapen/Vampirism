package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blockentity.VampireBeaconBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VampireBeaconRenderer implements BlockEntityRenderer<VampireBeaconBlockEntity> {

    public static final ResourceLocation BEACON_BEAM_LOCATION = VResourceLocation.mc("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 1024;

    public VampireBeaconRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(VampireBeaconBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        long gameTime  = level.getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> list = blockEntity.getBeamSections();
        int j = 0;

        for (int k = 0; k < list.size(); ++k) {
            BeaconBlockEntity.BeaconBeamSection beaconblockentity$beaconbeamsection = list.get(k);
            renderBeaconBeam(poseStack, bufferSource, partialTick, gameTime, j, k == list.size() - 1 ? MAX_RENDER_Y : beaconblockentity$beaconbeamsection.getHeight(), beaconblockentity$beaconbeamsection.getColor());
            j += beaconblockentity$beaconbeamsection.getHeight();
        }
    }

    private static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, long gameTime, int yOffset, int height, int color) {
        BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BEACON_BEAM_LOCATION, partialTick, 1.0F, gameTime, yOffset, height, color, 0.2F, 0.25F);
    }

    @Override
    public boolean shouldRenderOffScreen(VampireBeaconBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(VampireBeaconBlockEntity blockEntity, Vec3 pCameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(pCameraPos.multiply(1.0D, 0.0D, 1.0D), this.getViewDistance());
    }

    @Override
    public AABB getRenderBoundingBox(VampireBeaconBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
