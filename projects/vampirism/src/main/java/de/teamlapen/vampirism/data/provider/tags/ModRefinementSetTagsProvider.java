package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.tags.FactionRefinementSetTags;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModRefinementSets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRefinementSetTagsProvider extends KeyTagProvider<IRefinementSet> {

    public ModRefinementSetTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, FactionRegistries.Keys.REFINEMENT_SET, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (ModRefinementSets.Definition set : ModRefinementSets.SETS) {
            this.tag(set.faction()).add(set.key());
            this.tag(set.rarity().tag).add(set.key());
            set.slots().forEach(slot -> this.tag(slot.tag).add(set.key()));
        }
    }
}
