package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.tileentity.AltarInfusionTileEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Altar of infusion
 */
public class AltarInfusionBlock extends VampirismBlockContainer {
    protected static final VoxelShape altarBase = makeShape();
    private final static String name = "altar_infusion";

    private static VoxelShape makeShape() {
        //base
        VoxelShape a = Block.box(5, 0, 5, 11, 4, 11);
        VoxelShape b = Block.box(2, 4, 2, 14, 5, 14);
        VoxelShape c = Block.box(1, 5, 1, 15, 6, 15);
        //side
        VoxelShape d1 = Block.box(1, 6, 1, 3, 7, 15);
        VoxelShape d2 = Block.box(1, 6, 1, 15, 7, 3);
        VoxelShape d3 = Block.box(15, 6, 15, 15, 7, 3);
        VoxelShape d4 = Block.box(15, 6, 15, 3, 7, 15);
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
        super(name, Properties.of(Material.STONE).strength(5).noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarInfusionTileEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return altarBase;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        AltarInfusionTileEntity te = (AltarInfusionTileEntity) worldIn.getBlockEntity(pos);
        //If empty hand and can start -> StartAdvanced
        if (worldIn.isClientSide || te == null) return InteractionResult.SUCCESS;
        if (!Helper.isVampire(player)) {
            player.displayClientMessage(new TranslatableComponent("text.vampirism.altar_infusion.ritual.wrong_faction"), true);
            return InteractionResult.SUCCESS;
        }
        AltarInfusionTileEntity.Result result = te.canActivate(player, true);
        if (heldItem.isEmpty()) {
            if (result == AltarInfusionTileEntity.Result.OK) {
                te.startRitual(player);
                return InteractionResult.SUCCESS;
            }

        }
        //If non empty hand or missing tileInventory -> open GUI
        if (!heldItem.isEmpty() || result == AltarInfusionTileEntity.Result.INVMISSING) {
            if (te.getCurrentPhase() != AltarInfusionTileEntity.PHASE.NOT_RUNNING) {
                player.displayClientMessage(new TranslatableComponent("text.vampirism.altar_infusion.ritual_still_running"), true);
                return InteractionResult.SUCCESS;
            }
            player.openMenu(te);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, Level worldIn, BlockPos pos) {
        dropInventoryTileEntityItems(worldIn, pos);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.altar_infusion, AltarInfusionTileEntity::tick);
    }
}