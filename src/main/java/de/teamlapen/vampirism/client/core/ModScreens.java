package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.client.VIngameOverlays;
import de.teamlapen.vampirism.client.gui.screens.*;
import de.teamlapen.vampirism.client.gui.screens.taskboard.TaskBoardScreen;
import de.teamlapen.vampirism.core.ModContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import org.jetbrains.annotations.NotNull;

public class ModScreens {
    /**
     * Call on serial loading queue to avoid possible issues with parallel access
     */
    public static void registerScreensUnsafe() {
        MenuScreens.register(ModContainer.HUNTER_TABLE.get(), HunterTableScreen::new);
        MenuScreens.register(ModContainer.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronScreen::new);
        MenuScreens.register(ModContainer.WEAPON_TABLE.get(), WeaponTableScreen::new);
        MenuScreens.register(ModContainer.HUNTER_TRAINER.get(), HunterTrainerScreen::new);
        MenuScreens.register(ModContainer.HUNTER_BASIC.get(), HunterBasicScreen::new);
        MenuScreens.register(ModContainer.ALTAR_INFUSION.get(), AltarInfusionScreen::new);
        MenuScreens.register(ModContainer.BLOOD_GRINDER.get(), BloodGrinderScreen::new);
        MenuScreens.register(ModContainer.MINION.get(), MinionScreen::new);
        MenuScreens.register(ModContainer.TASK_MASTER.get(), TaskBoardScreen::new);
        MenuScreens.register(ModContainer.EXTENDED_POTION_TABLE.get(), PotionTableScreen::new);
        MenuScreens.register(ModContainer.VAMPIRISM.get(), VampirismContainerScreen::new);
        MenuScreens.register(ModContainer.ALCHEMICAL_TABLE.get(), AlchemyTableScreen::new);
        MenuScreens.register(ModContainer.VAMPIRE_BEACON.get(), VampireBeaconScreen::new);
        MenuScreens.register(ModContainer.REVERT_BACK.get(), InjectionChairRevertBackScreen::new);
    }

    static void registerScreenOverlays(@NotNull RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), VIngameOverlays.FACTION_LEVEL_ID, VIngameOverlays.FACTION_LEVEL_ELEMENT);
        event.registerAbove(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), VIngameOverlays.FACTION_RAID_BAR_ID, VIngameOverlays.FACTION_RAID_BAR_ELEMENT);
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), VIngameOverlays.BLOOD_BAR_ID, VIngameOverlays.BLOOD_BAR_ELEMENT);
        event.registerAboveAll(VIngameOverlays.ACTION_COOLDOWN_ID, VIngameOverlays.ACTION_COOLDOWN_ELEMENT);
        event.registerAboveAll(VIngameOverlays.ACTION_DURATION_ID, VIngameOverlays.ACTION_DURATION_ELEMENT);
    }
}
