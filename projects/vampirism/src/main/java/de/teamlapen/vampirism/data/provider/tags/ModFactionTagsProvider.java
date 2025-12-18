package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.tags.FactionTags;
import de.teamlapen.vampirism.REFERENCE;
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
        this.tag(ModFactionTags.HOSTILE_TOWARDS_NEUTRAL).add(de.teamlapen.vampirism.common.core.ModFactions.VAMPIRE.getRawKey());
        this.tag(ModFactionTags.IS_HUNTER).add(de.teamlapen.vampirism.common.core.ModFactions.HUNTER.getRawKey());
        this.tag(ModFactionTags.IS_VAMPIRE).add(de.teamlapen.vampirism.common.core.ModFactions.VAMPIRE.getRawKey());
        this.tag(ModFactionTags.HAS_LORD_SKILLS).add(de.teamlapen.vampirism.common.core.ModFactions.VAMPIRE.getRawKey(), de.teamlapen.vampirism.common.core.ModFactions.HUNTER.getRawKey());
        this.tag(ModFactionTags.ALL_FACTIONS).addTag(ModFactionTags.IS_HUNTER).addTag(ModFactionTags.IS_VAMPIRE);
        this.tag(ModFactionTags.USE_GARLIC_BREAD).addTags(ModFactionTags.IS_HUNTER, ModFactionTags.IS_NEUTRAL);
        this.tag(ModFactionTags.CAN_RAID).addTags(ModFactionTags.IS_HUNTER, ModFactionTags.IS_VAMPIRE);
        this.tag(ModFactionTags.HAS_RANDOM_RAID).addTag(ModFactionTags.CAN_RAID);
        this.tag(ModFactionTags.HUNTER_MINION_TARGETS).addTag(ModFactionTags.IS_VAMPIRE);
        this.tag(ModFactionTags.VAMPIRE_MINION_TARGETS).addTag(FactionTags.NOT_NEUTRAL).remove(ModFactionTags.IS_VAMPIRE);
    }
}
