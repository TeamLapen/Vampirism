package de.teamlapen.vampirism.entity.player.hunter;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * * Default Implementation of {@link IHunterPlayer} for the entity capability which is never used, since a default implementation, does not accept constructor parameter.
 */
public class HunterPlayerDefaultImpl implements IHunterPlayer {

    public HunterPlayerDefaultImpl() {
        VampirismMod.log.e("HunterPlayerCapability", "Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");

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
    public IPlayableFaction<IHunterPlayer> getFaction() {
        return null;
    }

    @Override
    public long getLastComebackCall() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getMaxMinionCount() {
        return 0;
    }

    @Override
    public EntityLivingBase getMinionTarget() {
        return null;
    }

    @Override
    public Predicate<? super Entity> getNonFriendlySelector(boolean otherFactionPlayers) {
        return null;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return null;
    }

    @Override
    public EntityPlayer getRepresentingPlayer() {
        return null;
    }

    @Override
    public ISkillHandler<IHunterPlayer> getSkillHandler() {
        return null;
    }

    @Override
    public double getTheDistanceSquared(Entity e) {
        return 0;
    }

    @Override
    public UUID getThePersistentID() {
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
    public boolean isTheEntityAlive() {
        return false;
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {

    }
}
