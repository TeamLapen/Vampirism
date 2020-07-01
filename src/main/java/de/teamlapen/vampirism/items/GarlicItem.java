package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

/**
 * Item for the garlic plant
 */
public class GarlicItem extends VampirismItem implements IPlantable, IFactionExclusiveItem {
    private final static String regName = "item_garlic";

    public GarlicItem() {
        super(regName, new Properties().group(VampirismMod.creativeTab));
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public BlockState getPlant(IBlockReader world, BlockPos pos) {
        return ModBlocks.garlic.getDefaultState();
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.Crop;
    }


    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getItem();
        BlockPos pos = ctx.getPos();
        if (ctx.getFace() != Direction.UP) {
            return ActionResultType.FAIL;
        } else if (ctx.getPlayer() != null && !ctx.getPlayer().canPlayerEdit(pos.offset(ctx.getFace()), ctx.getFace(), stack)) {
            return ActionResultType.FAIL;
        } else if (ctx.getWorld().getBlockState(pos).getBlock().canSustainPlant(ctx.getWorld().getBlockState(pos), ctx.getWorld(), pos, Direction.UP, this) && ctx.getWorld().isAirBlock(pos.up())) {
            ctx.getWorld().setBlockState(pos.up(), getPlant(ctx.getWorld(), pos));
            stack.shrink(1);
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.FAIL;
        }
    }
}
