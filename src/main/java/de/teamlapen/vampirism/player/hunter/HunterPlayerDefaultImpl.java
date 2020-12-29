package de.teamlapen.vampirism.player.hunter;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * * Default Implementation of {@link IHunterPlayer} for the entity capability which is never used, since a default implementation, does not accept constructor parameter.
 */
@SuppressWarnings("ConstantConditions")
@Deprecated
class HunterPlayerDefaultImpl implements IHunterPlayer {

    private final static Logger LOGGER = LogManager.getLogger(HunterPlayerDefaultImpl.class);

    public HunterPlayerDefaultImpl() {
        LOGGER.error("Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");

    }

    @Override
    public boolean canLeaveFaction() {
        return false;
    }

    @Override
    public IActionHandler<IHunterPlayer> getActionHandler() {
        return null;
    }

    @Override
    public IFaction getDisguisedAs() {
        return null;
    }


    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }


    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        return null;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return null;
    }

    @Override
    public PlayerEntity getRepresentingPlayer() {
        return null;
    }

    @Override
    public ISkillHandler<IHunterPlayer> getSkillHandler() {
        return null;
    }

    @Override
    public boolean isDisguised() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }


    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {

    }

    @Nonnull
    @Override
    public ITaskManager getTaskManager() {
        return null;
    }
}
