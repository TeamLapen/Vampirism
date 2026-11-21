package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.heritage.HeritageLibrary;
import de.teamlapen.vampirism.common.world.items.component.HeritageResearch;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A heritage book shows the heritage of the player or vampire it is named after, once that heritage was researched on
 * a lectern surrounded by bookshelves (see {@link HeritageLibrary}).
 */
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

    /**
     * @return research progress in ticks for the current name of the book. Research done under another name does not count
     */
    public static int getResearchProgress(ItemStack stack) {
        String targetName = getTargetName(stack);
        HeritageResearch research = stack.get(ModDataComponents.HERITAGE_RESEARCH);
        return targetName != null && research != null && research.subject().equals(targetName) ? research.progress() : 0;
    }

    public static boolean isResearched(ItemStack stack) {
        return getResearchProgress(stack) >= HeritageLibrary.RESEARCH_DURATION;
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

    @Override
    public boolean isFoil(ItemStack stack) {
        return isResearched(stack) || super.isFoil(stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (getTargetName(stack) == null) {
            return;
        }
        int progress = getResearchProgress(stack);
        String status = progress >= HeritageLibrary.RESEARCH_DURATION ? "researched" : progress > 0 ? "researching" : "blank";
        tooltipComponents.accept(Component.translatable("tooltip.vampirism.heritage_book." + status).withStyle(ChatFormatting.GRAY));
    }
}
