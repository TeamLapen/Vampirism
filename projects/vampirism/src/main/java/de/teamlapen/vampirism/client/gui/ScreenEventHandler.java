package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.common.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.blocks.TentBlock;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Add a button to the inventory screen that allows opening the skill menu from there
 */
public class ScreenEventHandler {

    @SubscribeEvent
    public void onInitGuiEventPost(ScreenEvent.Init.@NotNull Post event) {
        if (event.getScreen() instanceof InBedChatScreen) {
            Player p = event.getScreen().getMinecraft().player;
            if (p != null && p.isSleeping()) {
                GuiEventListener l = event.getScreen().children().get(1);
                if (l instanceof AbstractWidget leaveButton) {
                    p.getSleepingPos().map(pos -> p.level().getBlockState(pos).getBlock()).map(block -> block instanceof TentBlock ? "text.vampirism.tent.stop_sleeping" : (block instanceof CoffinBlock ? "text.vampirism.coffin.stop_sleeping" : null)).ifPresent(newText -> {
                        leaveButton.setMessage(Component.translatable(newText));
                    });
                }
            }
        }
    }

}
