package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;


public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc> extends ForgeRegistryEntry<IMinionTask<?>> implements IMinionTask<T> {
    private ITextComponent name;

    @Override
    public ITextComponent getName() {
        if (name == null) {
            name = new TranslationTextComponent(Util.makeTranslationKey("minion_task", getRegistryName()));
        }
        return name;
    }
}
