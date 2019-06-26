package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.item.ItemTier;


/**
 * Mainly intended to be used by aggressive villagers.
 */
public class ItemPitchfork extends VampirismItemWeapon {

    private final static String regName = "pitchfork";

    public ItemPitchfork() {
        super(regName, ItemTier.IRON, 8, 0.27F, new Properties().group(VampirismMod.creativeTab));

    }
}
