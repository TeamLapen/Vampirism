package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;

public class VampirismPotion extends Potion {
    public VampirismPotion(String regName, @Nullable String baseName, EffectInstance... effects) {
        super(baseName, effects);
        this.setRegistryName(REFERENCE.MODID, regName);
    }
}
