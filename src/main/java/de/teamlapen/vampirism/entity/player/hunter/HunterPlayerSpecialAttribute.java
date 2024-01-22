package de.teamlapen.vampirism.entity.player.hunter;

import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.items.HunterCoatItem;
import org.jetbrains.annotations.Nullable;

/**
 * Stores special attributes that can be activated by skills or actions.
 * One attribute should only be modified by ONE skill/action.
 */
public class HunterPlayerSpecialAttribute {

    /**
     * Value from {@link HunterCoatItem#isFullyEquipped(net.minecraft.world.entity.player.Player)} cached in {@link HunterPlayer#onUpdate()}
     * Null if not fully equipped, otherwise min tier
     */
    @Nullable
    public IItemWithTier.TIER fullHunterCoat;

    /**
     * Set while blessing an item.
     */
    @Nullable
    public ISoundReference blessingSoundReference;

}
