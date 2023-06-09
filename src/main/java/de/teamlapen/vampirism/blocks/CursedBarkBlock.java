package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public abstract class CursedBarkBlock extends Block {

    private static final VoxelShape shape =  Shapes.empty();

    public CursedBarkBlock() {
        super(BlockBehaviour.Properties.of().noCollission().replaceable().strength(0.0F).pushReaction(PushReaction.DESTROY).ignitedByLava().isViewBlocking(UtilLib::never).sound(SoundType.VINE));
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }

    protected void moveEntityTo(@NotNull Level level, @NotNull Entity entity, @NotNull BlockPos targetPos) {
        if (targetPos.equals(entity.blockPosition())) return;
        Vec3 thrust = new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ()).subtract(entity.getX(), entity.getY(), entity.getZ()).normalize().scale(0.04);
        if (!entity.onGround()) {
            thrust = thrust.scale(0.3d);
        }
        entity.setDeltaMovement(entity.getDeltaMovement().add(thrust));

        if (!level.isClientSide) {
            if (entity instanceof Player) {
                VampirePlayer.getOpt(((Player) entity)).ifPresent(vampire -> {
                    if (vampire.getRemainingBarkTicks() == 0) {
                        vampire.removeBlood(0.02f);
                        vampire.increaseRemainingBarkTicks(40);
                    }
                });
            } else {
                ExtendedCreature.getSafe(entity).ifPresent(creature -> {
                    if (((ExtendedCreature) creature).getRemainingBarkTicks() == 0) {
                        creature.setBlood(creature.getBlood() - 1);
                        ((ExtendedCreature) creature).sync();
                        ((ExtendedCreature) creature).increaseRemainingBarkTicks(40);
                    }

                });
            }
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        return shape;
    }

    @Override
    protected void spawnDestroyParticles(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state) {
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }
}
