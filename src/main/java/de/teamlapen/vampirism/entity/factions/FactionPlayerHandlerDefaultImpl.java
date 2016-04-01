package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

/**
 * Default Implementation of {@link IFactionPlayerHandler} for the entity capability which is never used, since a default implementation, does not accept constructor parameter.
 */
public class FactionPlayerHandlerDefaultImpl implements IFactionPlayerHandler {

    public FactionPlayerHandlerDefaultImpl() {
        VampirismMod.log.e("FactionPlayerHandlerCapability", "Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");

    }

    @Override
    public boolean canJoin(IPlayableFaction faction) {
        return false;
    }

    @Override
    public boolean canLeaveFaction() {
        return false;
    }

    @Override
    public IPlayableFaction getCurrentFaction() {
        return null;
    }

    @Override
    public IFactionPlayer getCurrentFactionPlayer() {
        return null;
    }

    @Override
    public int getCurrentLevel() {
        return 0;
    }

    @Override
    public int getCurrentLevel(IPlayableFaction f) {
        return 0;
    }

    @Override
    public boolean isInFaction(IPlayableFaction f) {
        return false;
    }

    @Override
    public void joinFaction(IPlayableFaction faction) {

    }

    @Override
    public boolean setFactionAndLevel(IPlayableFaction faction, int level) {
        return false;
    }

    @Override
    public boolean setFactionLevel(IPlayableFaction faction, int level) {
        return false;
    }
}
