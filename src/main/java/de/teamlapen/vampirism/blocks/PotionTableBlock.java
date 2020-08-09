package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.tileentity.PotionTableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;


public class PotionTableBlock extends VampirismBlockContainer {
    protected static final VoxelShape shape = makeShape();
    private final static String regName = "potion_table";

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
        VoxelShape b = Block.makeCuboidShape(1, 1, 1, 15, 2, 15);
        VoxelShape c = Block.makeCuboidShape(2, 2, 2, 14, 9, 14);
        VoxelShape d = Block.makeCuboidShape(0, 9, 0, 16, 11, 16);
        return VoxelShapes.or(a, b, c, d);
    }

    public PotionTableBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(1f));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new PotionTableTileEntity();
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof PotionTableTileEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (PotionTableTileEntity) tile, buffer -> buffer.writeBoolean(((PotionTableTileEntity) tile).isExtended()));
                player.addStat(ModStats.interact_alchemical_cauldron);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState blockState, LivingEntity entity, ItemStack stack) {
        super.onBlockPlacedBy(world, blockPos, blockState, entity, stack);
        TileEntity tile = world.getTileEntity(blockPos);
        if (entity instanceof PlayerEntity && tile instanceof PotionTableTileEntity) {
            ((PotionTableTileEntity) tile).setOwnerID((PlayerEntity) entity);
        }
    }

}
