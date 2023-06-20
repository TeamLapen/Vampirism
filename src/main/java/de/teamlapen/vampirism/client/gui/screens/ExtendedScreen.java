package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * instead of mixin/coremod/AT let screens extend this to access certain attributes
 */
@OnlyIn(Dist.CLIENT)
public interface ExtendedScreen {

    /**
     * @return {@link de.teamlapen.vampirism.inventory.TaskMenu} of the screen
     */
    TaskMenu getTaskContainer();
}
