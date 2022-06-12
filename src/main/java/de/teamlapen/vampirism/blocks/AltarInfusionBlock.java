package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.AltarInfusionTileEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * Altar of infusion
 */
public class AltarInfusionBlock extends VampirismBlockContainer {
    protected static final VoxelShape altarBase = makeShape();

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

        return VoxelShapes.or(a, b, c, d1, d2, d3, d4, e1, e2, e3, e4, f1, f2, f3, f4, g, h1, h2, h3);
    }

    public AltarInfusionBlock() {
        super(Properties.of(Material.STONE).strength(5).noOcclusion());
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
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
    public TileEntity newBlockEntity(IBlockReader world) {
        return new AltarInfusionTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return altarBase;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        AltarInfusionTileEntity te = (AltarInfusionTileEntity) worldIn.getBlockEntity(pos);
        //If empty hand and can start -> StartAdvanced
        if (worldIn.isClientSide || te == null) return ActionResultType.SUCCESS;
        if (!Helper.isVampire(player)) {
            player.displayClientMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual.wrong_faction"), true);
            return ActionResultType.SUCCESS;
        }
        AltarInfusionTileEntity.Result result = te.canActivate(player, true);
        if (heldItem.isEmpty()) {
            if (result == AltarInfusionTileEntity.Result.OK) {
                te.startRitual(player);
                return ActionResultType.SUCCESS;
            }

        }
        //If non empty hand or missing tileInventory -> open GUI
        if (!heldItem.isEmpty() || result == AltarInfusionTileEntity.Result.INVMISSING) {
            if (te.getCurrentPhase() != AltarInfusionTileEntity.PHASE.NOT_RUNNING) {
                player.displayClientMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual_still_running"), true);
                return ActionResultType.SUCCESS;
            }
            player.openMenu(te);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, World worldIn, BlockPos pos) {
        dropInventoryTileEntityItems(worldIn, pos);
    }
}