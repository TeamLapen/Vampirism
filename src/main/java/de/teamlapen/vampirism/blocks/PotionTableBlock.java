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

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(0, 0, 0, 16, 1, 16);
        VoxelShape b = Block.box(1, 1, 1, 15, 2, 15);
        VoxelShape c = Block.box(2, 2, 2, 14, 9, 14);
        VoxelShape d = Block.box(0, 9, 0, 16, 11, 16);
        return VoxelShapes.or(a, b, c, d);
    }

    public PotionTableBlock() {
        super(Properties.of(Material.METAL).strength(1f).noOcclusion());
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new PotionTableTileEntity();
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public void setPlacedBy(World world, BlockPos blockPos, BlockState blockState, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, blockPos, blockState, entity, stack);
        TileEntity tile = world.getBlockEntity(blockPos);
        if (entity instanceof PlayerEntity && tile instanceof PotionTableTileEntity) {
            ((PotionTableTileEntity) tile).setOwnerID((PlayerEntity) entity);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide && player instanceof ServerPlayerEntity) {
            TileEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof PotionTableTileEntity) {
                if (((PotionTableTileEntity) tile).canOpen(player)) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (PotionTableTileEntity) tile, buffer -> buffer.writeBoolean(((PotionTableTileEntity) tile).isExtended()));
                    player.awardStat(ModStats.interact_alchemical_cauldron);
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof PotionTableTileEntity) {
            for (int i = 0; i < 8; ++i) {
                this.dropItem(worldIn, pos, ((PotionTableTileEntity) te).removeItemNoUpdate(i));
            }
        }
    }

}
