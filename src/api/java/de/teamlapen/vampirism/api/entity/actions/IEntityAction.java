package de.teamlapen.vampirism.api.entity.actions;

import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IEntityAction extends IForgeRegistryEntry<IEntityAction> {

    int getDuration(int level);
    
    int getCooldown(int level);
}
