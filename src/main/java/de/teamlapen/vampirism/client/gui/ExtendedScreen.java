package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.inventory.container.TaskContainer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * instead of mixin/coremod/AT let screens extend this to access certain attributes
 */
@OnlyIn(Dist.CLIENT)
public interface ExtendedScreen {

    /**
     * @return {@link ItemRenderer} of the screen
     */
    ItemRenderer getItemRenderer();

    /**
     * @return {@link TaskContainer} of the screen
     */
    TaskContainer getTaskContainer();
}
