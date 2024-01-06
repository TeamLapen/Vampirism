package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.inventory.TaskMenu;



/**
 * instead of mixin/coremod/AT let screens extend this to access certain attributes
 */
public interface ExtendedScreen {

    /**
     * @return {@link de.teamlapen.vampirism.inventory.TaskMenu} of the screen
     */
    TaskMenu getTaskContainer();
}
