package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IVampireBiome extends IFactionBiome, IForgeRegistryEntry<Biome> {

    default void noSunDamageRegister() {
        VampirismAPI.sundamageRegistry().addNoSundamageBiome(this.getRegistryName());
    }

    @Override
    default IFaction<?> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }
}
