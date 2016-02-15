package de.teamlapen.vampirism.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

/**
 * Base class for Vampirism's potions
 */
public class VampirismPotion extends Potion {
    public VampirismPotion(ResourceLocation location, boolean badEffect, int potionColor) {
        super(location, badEffect, potionColor);
    }
}
