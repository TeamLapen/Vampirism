package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.village.Village;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface IVampirismVillage {

    @Nullable
    IFaction getControllingFaction();

    @Nonnull
    Village getVillage();

}
