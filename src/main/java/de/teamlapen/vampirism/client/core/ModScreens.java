package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.inventory.container.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
    /**
     * Call on serial loading queue to avoid possible issues with parallel access
     */
    @SuppressWarnings("RedundantTypeArguments")
    public static void registerScreensUnsafe() {
        ScreenManager.<HunterTableContainer, HunterTableScreen>register(ModContainer.HUNTER_TABLE.get(), HunterTableScreen::new);
        ScreenManager.<AlchemicalCauldronContainer, AlchemicalCauldronScreen>register(ModContainer.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronScreen::new);
        ScreenManager.<WeaponTableContainer, WeaponTableScreen>register(ModContainer.WEAPON_TABLE.get(), WeaponTableScreen::new);
        ScreenManager.<HunterTrainerContainer, HunterTrainerScreen>register(ModContainer.HUNTER_TRAINER.get(), HunterTrainerScreen::new);
        ScreenManager.<HunterBasicContainer, HunterBasicScreen>register(ModContainer.HUNTER_BASIC.get(), HunterBasicScreen::new);
        ScreenManager.<AltarInfusionContainer, AltarInfusionScreen>register(ModContainer.ALTAR_INFUSION.get(), AltarInfusionScreen::new);
        ScreenManager.<BloodGrinderContainer, BloodGrinderScreen>register(ModContainer.BLOOD_GRINDER.get(), BloodGrinderScreen::new);
        ScreenManager.<MinionContainer, MinionScreen>register(ModContainer.MINION.get(), MinionScreen::new);
        ScreenManager.register(ModContainer.TASK_MASTER.get(), TaskBoardScreen::new);
        ScreenManager.register(ModContainer.EXTENDED_POTION_TABLE.get(), PotionTableScreen::new);
        ScreenManager.register(ModContainer.VAMPIRISM.get(), VampirismScreen::new);
        ScreenManager.register(ModContainer.ALCHEMICAL_TABLE.get(), AlchemyTableScreen::new);
    }
}
