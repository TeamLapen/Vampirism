package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HeritageBookItem extends Item {

    public HeritageBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Component targetName = stack.get(DataComponents.CUSTOM_NAME);
        if (targetName == null || targetName.getString().isBlank()) {
            if (level.isClientSide()) {
                player.sendOverlayMessage(Component.translatable("message.vampirism.heritage_book.needs_name"));
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            VampirismMod.proxy.displayHeritageBookScreen(targetName.getString());
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
