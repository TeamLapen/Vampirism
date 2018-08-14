package de.teamlapen.vampirism.world.villages;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import net.minecraft.village.Village;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class VampirismVillageDefaultImpl implements IVampirismVillage {
    @Nullable
    @Override
    public IFaction getControllingFaction() {
        return null;
    }

    @Nonnull
    @Override
    public Village getVillage() {
        return null;
    }
}
