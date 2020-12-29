package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public boolean canJoin(IPlayableFaction<? extends IFactionPlayer<?>> faction) {
        return false;
    }

    @Override
    public boolean canLeaveFaction() {
        return false;
    }

    @Nullable
    @Override
    public IPlayableFaction<? extends IFactionPlayer<?>> getCurrentFaction() {
        return null;
    }

    @Nonnull
    @Override
    public Optional<? extends IFactionPlayer<?>> getCurrentFactionPlayer() {
        return Optional.empty();
    }

    @Override
    public int getCurrentLevel() {
        return 0;
    }

    @Override
    public int getCurrentLevel(IPlayableFaction<? extends IFactionPlayer<?>> f) {
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

    @Nonnull
    @Override
    public PlayerEntity getPlayer() {
        return null;
    }

    @Nullable
    @Override
    public ITextComponent getLordTitle() {
        return null;
    }

    @Override
    public boolean isInFaction(IPlayableFaction<? extends IFactionPlayer<?>> f) {
        return false;
    }

    @Override
    public void joinFaction(@Nonnull IPlayableFaction<? extends IFactionPlayer<?>> faction) {

    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        return false;
    }

    @Override
    public boolean setFactionAndLevel(IPlayableFaction<? extends IFactionPlayer<?>> faction, int level) {
        return false;
    }

    @Override
    public boolean setLordLevel(int level) {
        return false;
    }

    @Override
    public boolean setFactionLevel(@Nonnull IPlayableFaction<? extends IFactionPlayer<?>> faction, int level) {
        return false;
    }
}
