package de.teamlapen.vampirism.api.world.items.components;

import de.teamlapen.vampirism.api.world.items.IArrowContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Used to store projectiles in an item. Such as an {@link IArrowContainer}
 */
public interface IContainedProjectiles {

    /**
     * @return The contained projectiles
     */
    List<ItemStack> getProjectiles();
}
