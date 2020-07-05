package de.teamlapen.vampirism.world.gen.biome;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.IFactionBiome;

public abstract class VampireBiome extends VampirismBiome implements IFactionBiome {
    public VampireBiome(String regName, Builder builder) {
        super(regName, builder);
        VampirismAPI.sundamageRegistry().addNoSundamageBiome(this.getRegistryName());
    }

    @Override
    public IFaction<?> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }
}
