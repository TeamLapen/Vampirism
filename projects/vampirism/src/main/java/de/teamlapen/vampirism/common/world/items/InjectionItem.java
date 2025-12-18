package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
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
            if (stack.getCount() == 1) {
                player.setItemInHand(hand, stack.getCraftingRemainder());
            } else {
                stack.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, stack.getCraftingRemainder());
            }
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.displayClientMessage(Component.translatable("text.vampirism.injection.use_chair"), true);

        return super.use(level, player, hand);
    }
}
