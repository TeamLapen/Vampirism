package de.teamlapen.factions.client.core;

import de.teamlapen.factions.api.client.FactionOverlays;
import de.teamlapen.factions.client.FactionsClientMod;
import de.teamlapen.factions.client.gui.overlay.ActionCooldownOverlay;
import de.teamlapen.factions.client.gui.overlay.ActionDurationOverlay;
import de.teamlapen.factions.client.gui.overlay.FactionLevelOverlay;
import de.teamlapen.factions.client.gui.screens.FactionMenuScreen;
import de.teamlapen.factions.client.gui.screens.MinionScreen;
import de.teamlapen.factions.client.gui.screens.taskboard.TaskBoardScreen;
import de.teamlapen.factions.common.core.FactionMenus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class FactionScreens {

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(FactionMenus.FACTION_MENU.get(), FactionMenuScreen::new);
        event.register(FactionMenus.TASK_MASTER.get(), TaskBoardScreen::new);
        event.register(FactionMenus.MINION.get(), MinionScreen::new);
    }

    public static void registerScreenOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_LEVEL, FactionOverlays.FACTION_LEVEL_ID, new FactionLevelOverlay());
        event.registerAbove(VanillaGuiLayers.BOSS_OVERLAY, FactionOverlays.FACTION_RAID_BAR_ID, FactionsClientMod.services().bossInfoOverlay());
        event.registerAboveAll(FactionOverlays.ACTION_COOLDOWN_ID, new ActionCooldownOverlay<>());
        event.registerAboveAll(FactionOverlays.ACTION_DURATION_ID, new ActionDurationOverlay<>());
    }
}
