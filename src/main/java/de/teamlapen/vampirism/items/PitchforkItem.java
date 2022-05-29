package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.item.ItemTier;

/**
 * Mainly intended to be used by aggressive villagers.
 */
public class PitchforkItem extends VampirismItemWeapon {

    public PitchforkItem() {
        super(ItemTier.IRON, 6, -3, new Properties().tab(VampirismMod.creativeTab));
    }
}
