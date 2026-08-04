package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.tags.FactionActionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;

import java.util.concurrent.CompletableFuture;

public class ModActionTagsProvider extends KeyTagProvider<IAction<?>> {

    public ModActionTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, FactionRegistries.Keys.ACTION, lookupProvider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(FactionActionTags.SHOW_COOLDOWN_IN_HUD);
        tag(FactionActionTags.SHOW_DURATION_IN_HUD);
    }
}
