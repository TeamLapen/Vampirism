package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

/**
 * Item for the garlic plant
 */
public class ItemGarlic extends VampirismItem implements IPlantable {
    private final static String regName = "item_garlic";

    public ItemGarlic() {
        super(regName, new Properties());
    }

    @Override
    public IBlockState getPlant(IBlockReader world, BlockPos pos) {
        return ModBlocks.garlic.getDefaultState();
    }

    @Override
    public EnumPlantType getPlantType(IBlockReader world, BlockPos pos) {
        return EnumPlantType.Crop;
    }


    @Override
    public EnumActionResult onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getItem();
        BlockPos pos = ctx.getPos();
        if (ctx.getFace() != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        } else if (ctx.getPlayer() != null && !ctx.getPlayer().canPlayerEdit(pos.offset(ctx.getFace()), ctx.getFace(), stack)) {
            return EnumActionResult.FAIL;
        } else if (ctx.getWorld().getBlockState(pos).getBlock().canSustainPlant(ctx.getWorld().getBlockState(pos), ctx.getWorld(), pos, EnumFacing.UP, this) && ctx.getWorld().isAirBlock(pos.up())) {
            ctx.getWorld().setBlockState(pos.up(), getPlant(ctx.getWorld(), pos));
            stack.shrink(1);
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
