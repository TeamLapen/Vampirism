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
        ScreenManager.<HunterTableContainer, HunterTableScreen>register(ModContainer.hunter_table, HunterTableScreen::new);
        ScreenManager.<AlchemicalCauldronContainer, AlchemicalCauldronScreen>register(ModContainer.alchemical_cauldron, AlchemicalCauldronScreen::new);
        ScreenManager.<WeaponTableContainer, WeaponTableScreen>register(ModContainer.weapon_table, WeaponTableScreen::new);
        ScreenManager.<HunterTrainerContainer, HunterTrainerScreen>register(ModContainer.hunter_trainer, HunterTrainerScreen::new);
        ScreenManager.<HunterBasicContainer, HunterBasicScreen>register(ModContainer.hunter_basic, HunterBasicScreen::new);
        ScreenManager.<AltarInfusionContainer, AltarInfusionScreen>register(ModContainer.altar_infusion, AltarInfusionScreen::new);
        ScreenManager.<BloodGrinderContainer, BloodGrinderScreen>register(ModContainer.blood_grinder, BloodGrinderScreen::new);
        ScreenManager.<MinionContainer, MinionScreen>register(ModContainer.minion, MinionScreen::new);
        ScreenManager.register(ModContainer.task_master, TaskBoardScreen::new);
        ScreenManager.register(ModContainer.extended_potion_table, PotionTableScreen::new);
        ScreenManager.register(ModContainer.vampirism, VampirismScreen::new);
        ScreenManager.register(ModContainer.alchemical_table, AlchemicalTableScreen::new);
    }
}
