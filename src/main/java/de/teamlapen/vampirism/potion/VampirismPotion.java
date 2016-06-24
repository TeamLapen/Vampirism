package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.potion.Potion;

/**
 * Base class for Vampirism's potions
 */
public class VampirismPotion extends Potion {
    public VampirismPotion(String name, boolean badEffect, int potionColor) {
        super(badEffect, potionColor);
        this.setRegistryName(REFERENCE.MODID, name);
        this.setPotionName("effect.vampirism." + name);
    }
}
