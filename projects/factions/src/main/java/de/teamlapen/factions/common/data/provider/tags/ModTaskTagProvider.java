package de.teamlapen.factions.common.data.provider.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.tasks.Task;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.tags.FactionTaskTags;
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
