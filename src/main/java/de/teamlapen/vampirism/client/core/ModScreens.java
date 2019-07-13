package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.core.ModContainer;
import net.minecraft.client.gui.ScreenManager;

public class ModScreens {
    public static void registerScreens() {
        ScreenManager.registerFactory(ModContainer.hunter_table, HunterTableScreen::new);
        ScreenManager.registerFactory(ModContainer.alchemical_cauldron, AlchemicalCauldronScreen::new);
        ScreenManager.registerFactory(ModContainer.blood_potion_table, BloodPotionTableScreen::new);
        ScreenManager.registerFactory(ModContainer.weapon_table, HunterWeaponTableScreen::new);
        ScreenManager.registerFactory(ModContainer.hunter_trainer, HunterTrainerScreen::new);
        ScreenManager.registerFactory(ModContainer.hunter_basic, HunterBasicScreen::new);
        ScreenManager.registerFactory(ModContainer.altar_infusion, AltarInfusionScreen::new);
        ScreenManager.registerFactory(ModContainer.blood_grinder, BloodGrinderScreen::new);
    }
}
