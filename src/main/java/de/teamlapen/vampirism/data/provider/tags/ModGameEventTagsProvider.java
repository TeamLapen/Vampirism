package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.tags.ModGameEventTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModGameEventTagsProvider extends TagsProvider<GameEvent> {

    public ModGameEventTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.GAME_EVENT, lookupProvider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        this.tag(ModGameEventTags.DARK_STALKER_IGNORE).add(GameEvent.STEP.key(), GameEvent.HIT_GROUND.key());
    }
}
