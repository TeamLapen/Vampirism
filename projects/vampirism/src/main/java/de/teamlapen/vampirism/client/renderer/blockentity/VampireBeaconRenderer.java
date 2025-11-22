package de.teamlapen.vampirism.client.renderer.blockentity;

import de.teamlapen.vampirism.common.blockentity.VampireBeaconBlockEntity;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VampireBeaconRenderer extends BeaconRenderer<VampireBeaconBlockEntity> {

    public VampireBeaconRenderer(BlockEntityRendererProvider.Context context) {
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
