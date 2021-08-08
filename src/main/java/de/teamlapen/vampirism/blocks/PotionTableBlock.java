package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.tileentity.PotionTableTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class PotionTableBlock extends VampirismBlockContainer {
    protected static final VoxelShape shape = makeShape();
    private final static String regName = "potion_table";

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(0, 0, 0, 16, 1, 16);
        VoxelShape b = Block.box(1, 1, 1, 15, 2, 15);
        VoxelShape c = Block.box(2, 2, 2, 14, 9, 14);
        VoxelShape d = Block.box(0, 9, 0, 16, 11, 16);
        return Shapes.or(a, b, c, d);
    }

    public PotionTableBlock() {
        super(regName, Properties.of(Material.METAL).strength(1f).noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionTableTileEntity(pos, state);
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos blockPos, BlockState blockState, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, blockPos, blockState, entity, stack);
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (entity instanceof Player && tile instanceof PotionTableTileEntity) {
            ((PotionTableTileEntity) tile).setOwnerID((Player) entity);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide && player instanceof ServerPlayer) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof PotionTableTileEntity) {
                if (((PotionTableTileEntity) tile).canOpen(player)) {
                    NetworkHooks.openGui((ServerPlayer) player, (PotionTableTileEntity) tile, buffer -> buffer.writeBoolean(((PotionTableTileEntity) tile).isExtended()));
                    player.awardStat(ModStats.interact_alchemical_cauldron);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void clearContainer(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof PotionTableTileEntity) {
            for (int i = 0; i < 8; ++i) {
                this.dropItem(worldIn, pos, ((PotionTableTileEntity) te).removeItemNoUpdate(i));
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTiles.potion_table, PotionTableTileEntity::tick);
    }
}
