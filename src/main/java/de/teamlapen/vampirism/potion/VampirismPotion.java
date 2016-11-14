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

    @Override
    public VampirismPotion setIconIndex(int p_76399_1_, int p_76399_2_) {
        super.setIconIndex(p_76399_1_, p_76399_2_);
        return this;
    }
}
