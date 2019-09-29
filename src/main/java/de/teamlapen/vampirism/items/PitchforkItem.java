package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.item.ItemTier;


/**
 * Mainly intended to be used by aggressive villagers.
 */
public class PitchforkItem extends VampirismItemWeapon {

    private final static String regName = "pitchfork";

    public PitchforkItem() {
        super(regName, ItemTier.IRON, 8, -3, new Properties().group(VampirismMod.creativeTab));

    }
}
