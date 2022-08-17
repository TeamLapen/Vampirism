package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

/**
 * Default Implementation of {@link IFactionPlayerHandler} for the entity capability which is never used, since a default implementation, does not accept constructor parameter.
 */
@SuppressWarnings("ConstantConditions")
@Deprecated
class FactionPlayerHandlerDefaultImpl implements IFactionPlayerHandler {

    private final static Logger LOGGER = LogManager.getLogger(FactionPlayerHandlerDefaultImpl.class);

    public FactionPlayerHandlerDefaultImpl() {
        LOGGER.error("Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");

    }


    @Override
    public boolean canJoin(IPlayableFaction<?> faction) {
        return false;
    }

    @Override
    public boolean canLeaveFaction() {
        return false;
    }

    @Nullable
    @Override
    public IPlayableFaction<?> getCurrentFaction() {
        return null;
    }

    @NotNull
    @Override
    public Optional<? extends IFactionPlayer<?>> getCurrentFactionPlayer() {
        return Optional.empty();
    }

    @Override
    public int getCurrentLevel() {
        return 0;
    }

    @Override
    public int getCurrentLevel(IPlayableFaction<?> f) {
        return 0;
    }

    @Override
    public float getCurrentLevelRelative() {
        return 0;
    }

    @Nullable
    @Override
    public IPlayableFaction<?> getLordFaction() {
        return null;
    }

    @Override
    public int getLordLevel() {
        return 0;
    }

    @Nullable
    @Override
    public Component getLordTitle() {
        return null;
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public boolean isInFaction(IFaction<?> f) {
        return false;
    }

    @Override
    public void joinFaction(@NotNull IPlayableFaction<?> faction) {

    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        return false;
    }

    @Override
    public boolean setFactionAndLevel(@Nullable IPlayableFaction<?> faction, int level) {
        return false;
    }

    @Override
    public boolean setFactionLevel(@NotNull IPlayableFaction<?> faction, int level) {
        return false;
    }

    @Override
    public boolean setLordLevel(int level) {
        return false;
    }

    @Override
    public void leaveFaction(boolean die) {

    }
}
