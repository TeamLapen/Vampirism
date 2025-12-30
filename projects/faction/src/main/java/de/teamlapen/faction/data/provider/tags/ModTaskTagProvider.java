package de.teamlapen.faction.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.tags.FactionTaskTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;

import java.util.concurrent.CompletableFuture;

public class ModTaskTagProvider extends KeyTagProvider<Task> {

    public ModTaskTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, FactionRegistries.Keys.TASK, provider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(FactionTaskTags.HAS_FACTION);
        this.tag(FactionTaskTags.AWARDS_LORD_LEVEL);
        this.tag(FactionTaskTags.IS_UNIQUE)
                .addTag(FactionTaskTags.AWARDS_LORD_LEVEL);
    }
}
