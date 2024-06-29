package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

/**
 * Item for the garlic plant
 */
public class GarlicItem extends Item implements IPlantable, IFactionExclusiveItem {

    public GarlicItem() {
        super(new Properties());
    }

    @Override
    public @NotNull TagKey<IFaction<?>> getExclusiveFaction(@NotNull ItemStack stack) {
        return ModFactionTags.IS_HUNTER;
    }

    @Override
    public @NotNull BlockState getPlant(BlockGetter world, BlockPos pos) {
        return ModBlocks.GARLIC.get().defaultBlockState();
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.CROP;
    }


    @NotNull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext ctx) {
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
