package de.teamlapen.vampirism.blocks;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Altar of infusion
 */
public class AltarInfusionBlock extends VampirismBlockContainer {
    protected static final VoxelShape altarBase = makeShape();

    private static @NotNull VoxelShape makeShape() {
        //base
        VoxelShape a = Block.box(5, 0, 5, 11, 4, 11);
        VoxelShape b = Block.box(2, 4, 2, 14, 5, 14);
        VoxelShape c = Block.box(1, 5, 1, 15, 6, 15);
        //side
        VoxelShape d1 = Block.box(1, 6, 1, 3, 7, 15);
        VoxelShape d2 = Block.box(1, 6, 1, 15, 7, 3);
        VoxelShape d3 = Block.box(15, 6, 3, 15, 7, 15);
        VoxelShape d4 = Block.box(3, 6, 15, 15, 7, 15);
        //pillar
        VoxelShape e1 = Block.box(1, 6, 1, 3, 12, 3);
        VoxelShape e2 = Block.box(13, 6, 1, 15, 12, 3);
        VoxelShape e3 = Block.box(13, 6, 13, 15, 12, 15);
        VoxelShape e4 = Block.box(1, 6, 13, 3, 12, 15);
        //pillar top
        VoxelShape f1 = Block.box(1, 12, 1, 2, 13, 2);
        VoxelShape f2 = Block.box(14, 12, 1, 15, 13, 2);
        VoxelShape f3 = Block.box(1, 12, 14, 2, 13, 15);
        VoxelShape f4 = Block.box(14, 12, 14, 15, 13, 15);
        //middle base
        VoxelShape g = Block.box(5, 6, 5, 11, 7, 11);
        //blood
        VoxelShape h1 = Block.box(5, 9, 5, 11, 11, 11);
        VoxelShape h2 = Block.box(7, 7, 7, 9, 13, 9);
        VoxelShape h3 = Block.box(6, 8, 6, 10, 12, 10);

        return Shapes.or(a, b, c, d1, d2, d3, d4, e1, e2, e3, e4, f1, f2, f3, f4, g, h1, h2, h3);
    }

    public AltarInfusionBlock() {
        super(Properties.of().mapColor(MapColor.STONE).strength(5).noOcclusion());
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AltarInfusionBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return altarBase;
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        AltarInfusionBlockEntity te = (AltarInfusionBlockEntity) worldIn.getBlockEntity(pos);
        //If empty hand and can start -> StartAdvanced
        if (worldIn.isClientSide || te == null) return InteractionResult.SUCCESS;
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
                        player.awardStat(ModStats.altar_of_infusion_rituals_performed);
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
        player.awardStat(ModStats.interact_with_altar_of_infusion);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, @NotNull Level worldIn, BlockPos pos) {
        dropInventoryTileEntityItems(worldIn, pos);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.ALTAR_INFUSION.get(), AltarInfusionBlockEntity::tick);
    }
}