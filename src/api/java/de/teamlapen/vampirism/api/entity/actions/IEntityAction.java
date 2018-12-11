package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraftforge.registries.IForgeRegistryEntry;
import javax.annotation.Nullable;

public interface IEntityAction extends IForgeRegistryEntry<IEntityAction> {

    @Nullable
    IFaction getFaction();

    void forceDeactivation(IVampirismEntity entity);
}
