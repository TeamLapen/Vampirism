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
import net.minecraft.item.Item;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

/**
 * Item for the garlic plant
 */
public class GarlicItem extends Item implements IPlantable, IFactionExclusiveItem {

    public GarlicItem() {
        super(new Properties().tab(VampirismMod.creativeTab));
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public BlockState getPlant(IBlockReader world, BlockPos pos) {
        return ModBlocks.GARLIC.get().defaultBlockState();
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.CROP;
    }


    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        BlockPos pos = ctx.getClickedPos();
        if (ctx.getClickedFace() != Direction.UP) {
            return ActionResultType.FAIL;
        } else if (ctx.getPlayer() != null && !ctx.getPlayer().mayUseItemAt(pos.relative(ctx.getClickedFace()), ctx.getClickedFace(), stack)) {
            return ActionResultType.FAIL;
        } else if (ctx.getLevel().getBlockState(pos).getBlock().canSustainPlant(ctx.getLevel().getBlockState(pos), ctx.getLevel(), pos, Direction.UP, this) && ctx.getLevel().isEmptyBlock(pos.above())) {
            ctx.getLevel().setBlockAndUpdate(pos.above(), getPlant(ctx.getLevel(), pos));
            stack.shrink(1);
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.FAIL;
        }
    }
}
