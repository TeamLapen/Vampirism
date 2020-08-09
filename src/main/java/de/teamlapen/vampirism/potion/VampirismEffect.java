package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

/**
 * Base class for Vampirism's potions
 */
public class VampirismEffect extends Effect {
    public VampirismEffect(String name, EffectType effectType, int potionColor) {
        super(effectType, potionColor);
        this.setRegistryName(REFERENCE.MODID, name);
    }
}
