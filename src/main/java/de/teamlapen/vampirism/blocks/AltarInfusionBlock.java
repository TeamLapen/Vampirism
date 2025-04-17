package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.AltarInfusionBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * Altar of infusion
 */
public class AltarInfusionBlock extends VampirismBlockContainer {

    public static final MapCodec<AltarInfusionBlock> CODEC = simpleCodec(AltarInfusionBlock::new);

    private static final VoxelShape SHAPE = Stream.of(Block.box(5, 0, 5, 11, 4, 11), Block.box(1, 4, 1, 15, 7, 15), Block.box(5, 7, 5, 11, 13, 11), Block.box(1, 7, 13, 3, 13, 15), Block.box(1, 7, 1, 3, 13, 3), Block.box(13, 7, 1, 15, 13, 3), Block.box(13, 7, 13, 15, 13, 15)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AltarInfusionBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarInfusionBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        AltarInfusionBlockEntity te = (AltarInfusionBlockEntity) level.getBlockEntity(pos);
        //If empty hand and can start -> StartAdvanced
        if (level.isClientSide || te == null) return InteractionResult.SUCCESS;
        if (!Helper.isVampire(player)) {
            player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual.wrong_faction"), true);
            return InteractionResult.SUCCESS;
        }
        if (!player.isShiftKeyDown()) {
            AltarInfusionBlockEntity.Result result = te.canActivate(player);
            switch (result) {
                case ISRUNNING -> {
                    player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_still_running"), true);
                    return InteractionResult.SUCCESS;
                }
                case NIGHTONLY -> {
                    player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_night_only"), true);
                    return InteractionResult.SUCCESS;
                }
                case STRUCTUREWRONG -> {
                    player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_missing_pillars"), true);
                    return InteractionResult.SUCCESS;
                }
                case INVMISSING -> player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_missing_times"), true);
                case OK -> {
                    if (heldItem.isEmpty()) {
                        player.awardStat(ModStats.ALTAR_OF_INFUSION_RITUALS_PERFORMED.get());
                        te.startRitual(player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            if (te.getCurrentPhase() != AltarInfusionBlockEntity.PHASE.NOT_RUNNING) {
                player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_still_running"), true);
                return InteractionResult.SUCCESS;
            }
        }
        player.openMenu(te);
        player.awardStat(ModStats.INTERACT_WITH_ALTAR_OF_INFUSION.get());
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, Level level, BlockPos pos) {
        dropInventoryTileEntityItems(level, pos);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.ALTAR_INFUSION.get(), AltarInfusionBlockEntity::tick);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}