package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.tileentity.AlchemicalTableTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemicalTableBlock extends ContainerBlock {
    public static final BooleanProperty HAS_BOTTLE = BlockStateProperties.HAS_BOTTLE_0;

    public AlchemicalTableBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(0.5F).lightLevel((p_235461_0_) -> 1).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(HAS_BOTTLE, false));
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader level) {
        return new AlchemicalTableTileEntity();
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        if (p_225533_2_.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
            if (tileentity instanceof AlchemicalTableTileEntity) {
                p_225533_4_.openMenu((AlchemicalTableTileEntity)tileentity);
//                p_225533_4_.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }

            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
        if (p_180633_5_.hasCustomHoverName()) {
            TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
            if (tileentity instanceof AlchemicalTableTileEntity) {
                ((AlchemicalTableTileEntity)tileentity).setCustomName(p_180633_5_.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState state1, boolean p_196243_5_) {
        if (!state.is(state1.getBlock())) {
            TileEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof AlchemicalTableTileEntity){
                InventoryHelper.dropContents(level, pos, ((AlchemicalTableTileEntity) tileEntity));
            }
            super.onRemove(state, level, pos, state1, p_196243_5_);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HAS_BOTTLE);
    }
}
