package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.core.DefaultFactions;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;

import java.util.concurrent.CompletableFuture;

public class ModFactionTagProvider extends KeyTagProvider<IFaction<?>> {

    public ModFactionTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, FactionRegistries.Keys.FACTION, provider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(de.teamlapen.faction.api.tags.FactionTags.ALL_FACTIONS)
                .addTag(de.teamlapen.faction.api.tags.FactionTags.IS_NEUTRAL);
        this.tag(de.teamlapen.faction.api.tags.FactionTags.CAN_RAID);
        this.tag(de.teamlapen.faction.api.tags.FactionTags.HAS_LORD_SKILLS);
        this.tag(de.teamlapen.faction.api.tags.FactionTags.HOSTILE_TOWARDS_NEUTRAL);
        this.tag(de.teamlapen.faction.api.tags.FactionTags.FRIENDLY_TOWARDS_NEUTRAL);
        this.tag(de.teamlapen.faction.api.tags.FactionTags.NOT_NEUTRAL)
                .addTag(de.teamlapen.faction.api.tags.FactionTags.ALL_FACTIONS)
                .remove(de.teamlapen.faction.api.tags.FactionTags.IS_NEUTRAL);
        this.tag(de.teamlapen.faction.api.tags.FactionTags.IS_NEUTRAL)
                .add(DefaultFactions.NEUTRAL.getRawKey());
        this.tag(de.teamlapen.faction.api.tags.FactionTags.HAS_RANDOM_RAID);
    }
}
