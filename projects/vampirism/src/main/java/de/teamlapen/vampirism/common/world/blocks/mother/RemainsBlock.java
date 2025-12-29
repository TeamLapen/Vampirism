package de.teamlapen.vampirism.common.world.blocks.mother;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.tags.ModBlockTags;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.blockentity.MotherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.Arrays;
import java.util.Optional;

public class RemainsBlock extends Block implements BonemealableBlock, IRemainsBlock {

    private final boolean vulnerable;
    private final boolean isVulnerability;

    public RemainsBlock(Properties properties, boolean vulnerable, boolean isVulnerability) {
        super(properties);
        this.vulnerable = vulnerable;
        this.isVulnerability = isVulnerability;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return pLevel.getBlockState(pPos.below()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isVulnerable(BlockState state) {
        return this.vulnerable;
    }

    @Override
    public boolean isMother(BlockState state) {
        return false;
    }

    @Override
    public boolean isVulnerability(BlockState state) {
        return this.isVulnerability;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos.below(), ModBlocks.CURSED_HANGING_ROOTS.get().defaultBlockState());
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            getMotherEntity(level, pos).ifPresent(a -> a.informAboutAttacker(serverPlayer));
        }
        if (level instanceof ServerLevel serverLevel) {
            DamageHandler.hurtModded(serverLevel, player, ModDamageSources::mother, 1);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        getMotherEntity(level, pos).ifPresent(MotherBlockEntity::onStructureBlockRemoved);
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            if (MotherTreeStructure.findMother(level, pos).isEmpty()) {
                level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
            }
            if (state.is(ModBlockTags.VULNERABLE_REMAINS)) {
                if (Arrays.stream(Direction.values()).allMatch(d -> level.getBlockState(pos.relative(d)).is(ModBlockTags.REMAINS))) {
                    level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
                }
            }
        }
    }

    @Override
    public void unFreeze(Level level, BlockPos pos, BlockState state) {
        if (this == ModBlocks.VULNERABLE_REMAINS.get()) {
            level.setBlock(pos, ModBlocks.ACTIVE_VULNERABLE_REMAINS.get().defaultBlockState(), 3);
        }
    }

    private Optional<MotherBlockEntity> getMotherEntity(LevelAccessor level, BlockPos pos) {
        return MotherTreeStructure.findMother(level, pos).map(pair -> {
            BlockEntity blockEntity = level.getBlockEntity(pair.getLeft());
            if (blockEntity instanceof MotherBlockEntity mother) {
                return mother;
            }
            return null;
        });
    }
}
