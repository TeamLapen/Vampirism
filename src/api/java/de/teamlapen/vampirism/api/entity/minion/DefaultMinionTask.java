package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * moved to main package
 */
@Deprecated
public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends ForgeRegistryEntry<IMinionTask<?, ?>> implements IMinionTask<T, Q> { //TODO 1.17 remove
    private ITextComponent name;

    @Override
    public ITextComponent getName() {
        if (name == null) {
            name = new TranslationTextComponent(Util.makeDescriptionId("minion_task", getRegistryName()));
        }
        return name;
    }
}
