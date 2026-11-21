package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class HeritageBookItem extends Item {

    public HeritageBookItem(Properties properties) {
        super(properties);
    }

    /**
     * @return the name of the player or vampire whose heritage this book shows, or null if the stack is not a named heritage book
     */
    public static @Nullable String getTargetName(ItemStack stack) {
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (!stack.is(ModItems.HERITAGE_BOOK) || customName == null || customName.getString().isBlank()) {
            return null;
        }
        return customName.getString();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        String targetName = getTargetName(player.getItemInHand(hand));
        if (targetName == null) {
            if (level.isClientSide()) {
                player.sendOverlayMessage(Component.translatable("message.vampirism.heritage_book.needs_name"));
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            VampirismMod.proxy.displayHeritageBookScreen(targetName);
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
