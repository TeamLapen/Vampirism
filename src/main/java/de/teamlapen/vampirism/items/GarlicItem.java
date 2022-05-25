package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Item for the garlic plant
 */
public class GarlicItem extends VampirismItem implements IPlantable, IFactionExclusiveItem {

    public GarlicItem() {
        super(new Properties().tab(VampirismMod.creativeTab));
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        return ModBlocks.garlic.get().defaultBlockState();
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.CROP;
    }


    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        BlockPos pos = ctx.getClickedPos();
        if (ctx.getClickedFace() != Direction.UP) {
            return InteractionResult.FAIL;
        } else if (ctx.getPlayer() != null && !ctx.getPlayer().mayUseItemAt(pos.relative(ctx.getClickedFace()), ctx.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        } else if (ctx.getLevel().getBlockState(pos).getBlock().canSustainPlant(ctx.getLevel().getBlockState(pos), ctx.getLevel(), pos, Direction.UP, this) && ctx.getLevel().isEmptyBlock(pos.above())) {
            ctx.getLevel().setBlockAndUpdate(pos.above(), getPlant(ctx.getLevel(), pos));
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }
}
