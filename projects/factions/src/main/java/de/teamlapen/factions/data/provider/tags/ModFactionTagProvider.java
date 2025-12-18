package de.teamlapen.factions.data.provider.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.core.DefaultFactions;
import de.teamlapen.factions.common.tags.FactionTags;
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
        this.tag(FactionTags.ALL_FACTIONS)
                .addTag(FactionTags.IS_NEUTRAL);
        this.tag(FactionTags.CAN_RAID);
        this.tag(FactionTags.HAS_LORD_SKILLS);
        this.tag(FactionTags.HOSTILE_TOWARDS_NEUTRAL);
        this.tag(FactionTags.NOT_NEUTRAL)
                .addTag(FactionTags.ALL_FACTIONS)
                .remove(FactionTags.IS_NEUTRAL);
        this.tag(FactionTags.IS_NEUTRAL)
                .add(DefaultFactions.NEUTRAL.getRawKey());
        this.tag(FactionTags.HAS_RANDOM_RAID);
    }
}
