package de.teamlapen.vampirism.common.blocks;

import de.teamlapen.lib.util.UtilLib;
import de.teamlapen.vampirism.api.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.common.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class CursedBarkBlock extends Block implements HolyWaterEffectConsumer {

    public CursedBarkBlock(Properties properties) {
        super(properties.noCollission().replaceable().strength(0.0F).pushReaction(PushReaction.DESTROY).ignitedByLava().isViewBlocking(UtilLib::never));
        ((FireBlock) Blocks.FIRE).setFlammable(this, 5, 5);
    }

    protected void moveEntityTo(Level level, Entity entity, BlockPos targetPos) {
        if (targetPos.equals(entity.blockPosition())) return;
        Vec3 thrust = new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ()).subtract(entity.getX(), entity.getY(), entity.getZ()).normalize().scale(0.04);
        if (!entity.onGround()) {
            thrust = thrust.scale(0.3d);
        }
        entity.setDeltaMovement(entity.getDeltaMovement().add(thrust));

        if (!level.isClientSide) {
            if (entity instanceof Player player) {
                VampirePlayer vampire = VampirePlayer.get(player);
                if (vampire.getRemainingBarkTicks() == 0) {
                    vampire.removeBlood(0.02f);
                    vampire.increaseRemainingBarkTicks(40);
                }
            } else {
                ExtendedCreature.getSafe(entity).ifPresent(creature -> {
                    if (creature.getRemainingBarkTicks() == 0) {
                        creature.setBlood(creature.getBlood() - 1);
                        creature.sync();
                        creature.increaseRemainingBarkTicks(40);
                    }

                });
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Shapes.empty();
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.Tier tier) {
        level.destroyBlock(pos, false);
    }
}
