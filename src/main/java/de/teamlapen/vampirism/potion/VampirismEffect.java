package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.Collections;
import java.util.List;

/**
 * Base class for Vampirism's potions
 */
public class VampirismEffect extends Effect {
    public VampirismEffect(String name, EffectType effectType, int potionColor) {
        super(effectType, potionColor);
        this.setRegistryName(REFERENCE.MODID, name);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return (this == ModEffects.neonatal || this == ModEffects.disguise_as_vampire) ? Collections.emptyList() : super.getCurativeItems();
    }
}
