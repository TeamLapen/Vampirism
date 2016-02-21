package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler implements ISyncable.ISyncableExtendedProperties, IFactionPlayerHandler {
    private final static String TAG = "FactionPlayerHandler";

    public static FactionPlayerHandler get(EntityPlayer player) {
        return (FactionPlayerHandler) player.getExtendedProperties(VampirismAPI.FACTION_PLAYER_HANDLER_PROP);
    }

    public static void register(EntityPlayer player) {
        player.registerExtendedProperties(VampirismAPI.FACTION_PLAYER_HANDLER_PROP, new FactionPlayerHandler(player));
    }
    private final EntityPlayer player;
    private PlayableFaction currentFaction = null;
    private int currentLevel = 0;

    private FactionPlayerHandler(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean canJoin(PlayableFaction faction) {
        return currentFaction == null;
    }

    @Override
    public boolean canLeaveFaction() {
        if (currentFaction == null) return true;
        return currentFaction.getProp(player).canLeaveFaction();
    }

    public void copyFrom(EntityPlayer old) {
        FactionPlayerHandler oldP = get(old);
        currentFaction = oldP.currentFaction;
        currentLevel = oldP.currentLevel;
        notifyFaction(oldP.currentFaction, oldP.currentLevel);
    }

    @Override
    public PlayableFaction getCurrentFaction() {
        return currentFaction;
    }

    @Override
    public IFactionPlayer getCurrentFactionPlayer() {
        return currentFaction == null ? null : currentFaction.getProp(player);
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public int getCurrentLevel(PlayableFaction f) {
        return isInFaction(f) ? currentLevel : 0;
    }

    @Override
    public String getPropertyKey() {
        return VampirismAPI.FACTION_PLAYER_HANDLER_PROP;
    }

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public boolean isInFaction(PlayableFaction f) {
        return currentFaction == f;
    }

    @Override
    public void joinFaction(PlayableFaction faction) {
        if (canJoin(faction)) {
            setFactionAndLevel(faction, 1);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey(getPropertyKey())) {
            NBTTagCompound nbt = compound.getCompoundTag(getPropertyKey());
            currentFaction = getFactionFromString(nbt.getString("faction"));
            if (currentFaction == null) {
                VampirismMod.log.w(TAG, "Could not find faction %s. Did mods change?", nbt.getString("faction"));
            } else {
                currentLevel = nbt.getInteger("level");
                notifyFaction(null, 0);
            }

        }
    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        PlayableFaction old = currentFaction;
        int oldLevel = currentLevel;
        String f = nbt.getString("faction");
        if ("null".equals(f)) {
            currentFaction = null;
            currentLevel = 0;
        } else {
            currentFaction = getFactionFromString(f);
            if (currentFaction == null) {
                VampirismMod.log.e(TAG, "Cannot find faction %s on client. You have to register factions on both sides!", f);
                currentLevel = 0;
            } else {
                currentLevel = nbt.getInteger("level");
            }
        }
        notifyFaction(old, oldLevel);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        if (currentFaction != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("faction", currentFaction.prop);
            nbt.setInteger("level", currentLevel);
            compound.setTag(getPropertyKey(), nbt);
        }
    }

    @Override
    public boolean setFactionAndLevel(PlayableFaction faction, int level) {
        PlayableFaction old = currentFaction;
        int oldLevel = currentLevel;
        if (currentFaction != null && (currentFaction != faction || level == 0)) {
            if (!currentFaction.getProp(player).canLeaveFaction()) {
                VampirismMod.log.i(TAG, "You cannot leave faction %s, it is prevented by respective mod", currentFaction.prop);
                return false;
            }
        }
        if (faction == null) {
            currentFaction = null;
            currentLevel = 0;
        } else {
            if (level < 0 || level > faction.getHighestReachableLevel()) {
                VampirismMod.log.w(TAG, "Level %d in faction %s cannot be reached", level, faction.prop);
                return false;
            }
            currentFaction = faction;
            currentLevel = level;
        }
        if (currentFaction == null) currentLevel = 0;
        else if (currentLevel == 0) currentFaction = null;
        notifyFaction(old, oldLevel);
        sync(old != currentFaction);
        return true;

    }

    @Override
    public boolean setFactionLevel(PlayableFaction faction, int level) {
        if (faction == currentFaction) {
            return setFactionAndLevel(faction, level);
        }
        return false;
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setString("faction", currentFaction == null ? "null" : currentFaction.prop);
        nbt.setInteger("level", currentLevel);
    }

    private PlayableFaction getFactionFromString(String f) {
        for (PlayableFaction p : FactionRegistry.getPlayableFactions()) {
            if (p.prop.equals(f)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Notify faction about changes.
     * {@link FactionPlayerHandler#currentFaction} and {@link FactionPlayerHandler#currentLevel} will be used as the new ones
     *
     * @param oldFaction
     * @param oldLevel
     */
    private void notifyFaction(PlayableFaction oldFaction, int oldLevel) {
        if (oldFaction != currentFaction && oldFaction != null) {
            VampirismMod.log.t("Leaving faction %s", oldFaction.prop);
            oldFaction.getProp(player).onLevelChanged(0, oldLevel);
        }
        if (currentFaction != null) {
            VampirismMod.log.t("Changing to %s %d", currentFaction, currentLevel);
            currentFaction.getProp(player).onLevelChanged(currentLevel, oldFaction == currentFaction ? oldLevel : 0);
        }
        if (currentFaction != oldFaction) {
            onChangedFaction();
        }
    }

    /**
     * Called when the faction has changed
     */
    private void onChangedFaction() {
        player.refreshDisplayName();
    }

    private void sync(boolean all) {
        HelperLib.sync(this, player, all);
    }


}
