package de.teamlapen.vampirism.api.entity.factions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDisguise {

    @NotNull
    IPlayableFaction<?> getOriginalFaction();

    @Nullable
    IPlayableFaction<?> getViewedFaction(@Nullable IFaction<?> viewerFaction);

    @Nullable
    default IPlayableFaction<?> getViewedFaction(@Nullable IFaction<?> viewerFaction, boolean ignoreDisguise) {
        return ignoreDisguise ? getOriginalFaction() : getViewedFaction(viewerFaction);
    }

    void disguiseAs(@Nullable IPlayableFaction<?> faction);

    void unDisguise();

    boolean isDisguised();
}
