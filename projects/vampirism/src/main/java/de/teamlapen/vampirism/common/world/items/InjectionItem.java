package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.blocks.InjectionChairBlock;
import de.teamlapen.vampirism.misc.sit.SitEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class InjectionItem extends Item {

    public InjectionItem(Properties properties) {
        super(properties);
    }

    public boolean handleInjection(Level level, BlockPos pos, Player player, IFactionPlayerHandler handler, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction) {
        return false;
    }

    public void consumeInjectionItem(ItemStack stack, Player player, InteractionHand hand) {
        if (!player.isCreative()) {
            player.setItemInHand(hand, Helper.shrinkItemStack(stack, player));
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getVehicle() instanceof SitEntity sitEntity) {
            BlockPos pos = sitEntity.blockPosition();
            if (level.getBlockState(sitEntity.blockPosition()).getBlock() instanceof InjectionChairBlock injectionChairBlock) {
                if (injectionChairBlock.handleInjections(player.getItemInHand(hand), this, level, pos, player, hand)) {
                    return InteractionResult.SUCCESS_SERVER;
                }
            }
        }

        // TODO: Fix hunter trainer houses

        return super.use(level, player, hand);
    }
}
