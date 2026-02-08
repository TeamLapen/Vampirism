package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.network.packets.server.ServerboundSimpleInputEvent;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.NotNull;

public class ScreenEventHandler {

    private static final WidgetSprites INVENTORY_SKILLS = new WidgetSprites(FIdentifier.mod("widget/inventory_skills"), FIdentifier.mod("widget/inventory_skills_highlighted"));
    private ImageButton button;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onButtonClicked(ScreenEvent.MouseButtonPressed.@NotNull Pre event) {//InventoryScreen changes layout if recipe book button is clicked. Unfortunately it does not propagate this to the screen children, so we need to use this
        if (event.getScreen() instanceof InventoryScreen && FactionConfig.client().addFactionMenuButtonToInventory.get() && FactionPlayerHandler.getCurrentFactionPlayer(event.getScreen().getMinecraft().player).isPresent()) {
            //Do the same thing MouseHelper would do. However, if GUI returns false on mouseclick it will be called again by MouseHelper
            if (event.getScreen().mouseClicked(event.getMouseButtonEvent(), event.isDoubleClick())) {
                event.setCanceled(true);
                if (button != null) {
                    button.setPosition(((InventoryScreen) event.getScreen()).getGuiLeft() + FactionConfig.client().factionMenuButtonXPos.get(), event.getScreen().height / 2 + FactionConfig.client().factionMenuButtonYPos.get());
                }
            }
        }
    }

    @SubscribeEvent
    public void onInitGuiEventPost(ScreenEvent.Init.@NotNull Post event) {
        if (event.getScreen() instanceof InventoryScreen && FactionConfig.client().addFactionMenuButtonToInventory.get() && FactionPlayerHandler.getCurrentFactionPlayer(event.getScreen().getMinecraft().player).isPresent()) {
            button = new ImageButton(((InventoryScreen) event.getScreen()).getGuiLeft() + FactionConfig.client().factionMenuButtonXPos.get(), event.getScreen().height / 2 + FactionConfig.client().factionMenuButtonYPos.get(), 20, 18, INVENTORY_SKILLS, (context) -> {
                FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.FACTION_MENU));
            });
            event.addListener(button);
        }
    }
}
