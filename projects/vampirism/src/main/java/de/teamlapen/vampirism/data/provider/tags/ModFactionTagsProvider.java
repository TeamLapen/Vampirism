package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModFactionTagsProvider extends KeyTagProvider<IFaction<?>> {

    public ModFactionTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, FactionRegistries.Keys.FACTION, lookupProvider, REFERENCE.MODID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        this.tag(FactionTags.HOSTILE_TOWARDS_NEUTRAL).add(ModFactions.VAMPIRE.getRawKey());
        this.tag(FactionTags.FRIENDLY_TOWARDS_NEUTRAL).add(ModFactions.HUNTER.getRawKey());
        this.tag(VampirismTags.Factions.IS_HUNTER).add(ModFactions.HUNTER.getRawKey());
        this.tag(VampirismTags.Factions.IS_VAMPIRE).add(ModFactions.VAMPIRE.getRawKey());
        this.tag(FactionTags.HAS_LORD_SKILLS).add(ModFactions.VAMPIRE.getRawKey(), ModFactions.HUNTER.getRawKey());
        this.tag(FactionTags.ALL_FACTIONS).addTag(VampirismTags.Factions.IS_HUNTER).addTag(VampirismTags.Factions.IS_VAMPIRE);
        this.tag(ModFactionTags.USE_GARLIC_BREAD).addTags(VampirismTags.Factions.IS_HUNTER, FactionTags.IS_NEUTRAL);
        this.tag(FactionTags.HAS_RANDOM_RAID).addTag(FactionTags.CAN_RAID);
        this.tag(ModFactionTags.HUNTER_MINION_TARGETS).addTag(VampirismTags.Factions.IS_VAMPIRE);
        this.tag(ModFactionTags.VAMPIRE_MINION_TARGETS).addTag(FactionTags.NOT_NEUTRAL).remove(VampirismTags.Factions.IS_VAMPIRE);
        this.tag(FactionTags.CAN_RAID)
                .add(ModFactions.HUNTER.getRawKey())
                .add(ModFactions.VAMPIRE.getRawKey());
    }
}
