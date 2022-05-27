package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.inventory.container.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
    /**
     * Call on serial loading queue to avoid possible issues with parallel access
     */
    @SuppressWarnings("RedundantTypeArguments")
    public static void registerScreensUnsafe() {
        MenuScreens.<HunterTableContainer, HunterTableScreen>register(ModContainer.HUNTER_TABLE.get(), HunterTableScreen::new);
        MenuScreens.<AlchemicalCauldronContainer, AlchemicalCauldronScreen>register(ModContainer.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronScreen::new);
        MenuScreens.<WeaponTableContainer, WeaponTableScreen>register(ModContainer.WEAPON_TABLE.get(), WeaponTableScreen::new);
        MenuScreens.<HunterTrainerContainer, HunterTrainerScreen>register(ModContainer.HUNTER_TRAINER.get(), HunterTrainerScreen::new);
        MenuScreens.<HunterBasicContainer, HunterBasicScreen>register(ModContainer.HUNTER_BASIC.get(), HunterBasicScreen::new);
        MenuScreens.<AltarInfusionContainer, AltarInfusionScreen>register(ModContainer.ALTAR_INFUSION.get(), AltarInfusionScreen::new);
        MenuScreens.<BloodGrinderContainer, BloodGrinderScreen>register(ModContainer.BLOOD_GRINDER.get(), BloodGrinderScreen::new);
        MenuScreens.<MinionContainer, MinionScreen>register(ModContainer.MINION.get(), MinionScreen::new);
        MenuScreens.register(ModContainer.TASK_MASTER.get(), TaskBoardScreen::new);
        MenuScreens.register(ModContainer.EXTENDED_POTION_TABLE.get(), PotionTableScreen::new);
        MenuScreens.register(ModContainer.VAMPIRISM.get(), VampirismScreen::new);
    }
}
