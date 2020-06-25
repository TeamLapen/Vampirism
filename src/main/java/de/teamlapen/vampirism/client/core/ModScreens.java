package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.inventory.container.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
    @SuppressWarnings("RedundantTypeArguments")
    public static void registerScreens() {
        ScreenManager.<HunterTableContainer, HunterTableScreen>registerFactory(ModContainer.hunter_table, HunterTableScreen::new);
        ScreenManager.<AlchemicalCauldronContainer, AlchemicalCauldronScreen>registerFactory(ModContainer.alchemical_cauldron, AlchemicalCauldronScreen::new);
        ScreenManager.<BloodPotionTableContainer, BloodPotionTableScreen>registerFactory(ModContainer.blood_potion_table, BloodPotionTableScreen::new);
        ScreenManager.<WeaponTableContainer, WeaponTableScreen>registerFactory(ModContainer.weapon_table, WeaponTableScreen::new);
        ScreenManager.<HunterTrainerContainer, HunterTrainerScreen>registerFactory(ModContainer.hunter_trainer, HunterTrainerScreen::new);
        ScreenManager.<HunterBasicContainer, HunterBasicScreen>registerFactory(ModContainer.hunter_basic, HunterBasicScreen::new);
        ScreenManager.<AltarInfusionContainer, AltarInfusionScreen>registerFactory(ModContainer.altar_infusion, AltarInfusionScreen::new);
        ScreenManager.<BloodGrinderContainer, BloodGrinderScreen>registerFactory(ModContainer.blood_grinder, BloodGrinderScreen::new);
        ScreenManager.<MinionContainer, MinionScreen>registerFactory(ModContainer.minion, MinionScreen::new);
    }
}
