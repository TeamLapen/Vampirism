package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.world.blocks.MotherTrophyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MotherTrophyBlockEntity extends BlockEntity {

    private static final double PLAYER_DETECT_RANGE = 8.0;
    private static final float ROTATION_SPEED = 0.2f;

    public float targetYaw;
    public float currentYaw;
    public float prevYaw;

    public MotherTrophyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOTHER_TROPHY.get(), pos, state);
        float initialYaw = state.getValue(MotherTrophyBlock.ROTATION) * 22.5f;
        this.targetYaw = initialYaw;
        this.currentYaw = initialYaw;
        this.prevYaw = initialYaw;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MotherTrophyBlockEntity blockEntity) {
        blockEntity.prevYaw = blockEntity.currentYaw;

        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        Player nearestPlayer = level.getNearestPlayer(centerX, pos.getY() + 0.5, centerZ, PLAYER_DETECT_RANGE, EntitySelector.NO_SPECTATORS);
        if (nearestPlayer != null && hasLineOfSight(level, pos, nearestPlayer)) {
            double dx = nearestPlayer.getX() - centerX;
            double dz = nearestPlayer.getZ() - centerZ;
            blockEntity.targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        }

        float delta = Mth.wrapDegrees(blockEntity.targetYaw - blockEntity.currentYaw);
        blockEntity.currentYaw = Mth.wrapDegrees(blockEntity.currentYaw + delta * ROTATION_SPEED);
    }

    private static boolean hasLineOfSight(Level level, BlockPos pos, Player player) {
        Vec3 ghostEye = new Vec3(pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5);
        BlockHitResult result = level.clip(new ClipContext(ghostEye, player.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return result.getType() == HitResult.Type.MISS;
    }
}
