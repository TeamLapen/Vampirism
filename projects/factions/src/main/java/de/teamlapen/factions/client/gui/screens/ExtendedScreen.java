package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.common.inventory.ITaskMenu;


/**
 * instead of mixin/coremod/AT let screens extend this to access certain attributes
 */
public interface ExtendedScreen {

    /**
     * @return {@link ITaskMenu} of the screen
     */
    ITaskMenu getTaskContainer();
}
