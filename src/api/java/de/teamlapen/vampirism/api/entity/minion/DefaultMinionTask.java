package de.teamlapen.vampirism.api.entity.minion;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * moved to main package
 */
@Deprecated
public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends ForgeRegistryEntry<IMinionTask<?, ?>> implements IMinionTask<T, Q> { //TODO 1.17 remove
    private Component name;

    @Override
    public Component getName() {
        if (name == null) {
            name = new TranslatableComponent(Util.makeDescriptionId("minion_task", getRegistryName()));
        }
        return name;
    }
}
