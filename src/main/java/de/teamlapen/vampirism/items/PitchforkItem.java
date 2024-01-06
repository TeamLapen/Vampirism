package de.teamlapen.vampirism.items;

import net.minecraft.world.item.Tiers;

/**
 * Mainly intended to be used by aggressive villagers.
 */
public class PitchforkItem extends VampirismSwordItem {

    public PitchforkItem() {
        super(Tiers.IRON, 6, -3, new Properties());
    }
}
