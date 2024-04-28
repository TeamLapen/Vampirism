package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.client.VIngameOverlays;
import de.teamlapen.vampirism.client.gui.screens.*;
import de.teamlapen.vampirism.client.gui.screens.taskboard.TaskBoardScreen;
import de.teamlapen.vampirism.core.ModContainer;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;

public class ModScreens {
    /**
     * Call on serial loading queue to avoid possible issues with parallel access
     */
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModContainer.HUNTER_TABLE.get(), HunterTableScreen::new);
        event.register(ModContainer.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronScreen::new);
        event.register(ModContainer.WEAPON_TABLE.get(), WeaponTableScreen::new);
        event.register(ModContainer.HUNTER_TRAINER.get(), HunterTrainerScreen::new);
        event.register(ModContainer.HUNTER_BASIC.get(), HunterBasicScreen::new);
        event.register(ModContainer.ALTAR_INFUSION.get(), AltarInfusionScreen::new);
        event.register(ModContainer.BLOOD_GRINDER.get(), BloodGrinderScreen::new);
        event.register(ModContainer.MINION.get(), MinionScreen::new);
        event.register(ModContainer.TASK_MASTER.get(), TaskBoardScreen::new);
        event.register(ModContainer.EXTENDED_POTION_TABLE.get(), PotionTableScreen::new);
        event.register(ModContainer.VAMPIRISM.get(), VampirismContainerScreen::new);
        event.register(ModContainer.ALCHEMICAL_TABLE.get(), AlchemyTableScreen::new);
        event.register(ModContainer.VAMPIRE_BEACON.get(), VampireBeaconScreen::new);
        event.register(ModContainer.REVERT_BACK.get(), InjectionChairRevertBackScreen::new);
    }

    static void registerScreenOverlays(@NotNull RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR, VIngameOverlays.FACTION_LEVEL_ID, VIngameOverlays.FACTION_LEVEL_ELEMENT);
        event.registerAbove(VanillaGuiLayers.BOSS_OVERLAY, VIngameOverlays.FACTION_RAID_BAR_ID, VIngameOverlays.FACTION_RAID_BAR_ELEMENT);
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, VIngameOverlays.BLOOD_BAR_ID, VIngameOverlays.BLOOD_BAR_ELEMENT);
        event.registerAboveAll(VIngameOverlays.ACTION_COOLDOWN_ID, VIngameOverlays.ACTION_COOLDOWN_ELEMENT);
        event.registerAboveAll(VIngameOverlays.ACTION_DURATION_ID, VIngameOverlays.ACTION_DURATION_ELEMENT);
    }
}
