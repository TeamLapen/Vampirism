package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.world.item.Tiers;

/**
 * Mainly intended to be used by aggressive villagers.
 */
public class PitchforkItem extends VampirismItemWeapon {

    private final static String regName = "pitchfork";

    public PitchforkItem() {
        super(regName, Tiers.IRON, 6, -3, new Properties().tab(VampirismMod.creativeTab));
    }
}
