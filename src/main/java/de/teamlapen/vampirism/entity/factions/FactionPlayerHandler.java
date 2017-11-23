package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.event.FactionEvent;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler implements ISyncable.ISyncableEntityCapabilityInst, IFactionPlayerHandler {
    @CapabilityInject(IFactionPlayerHandler.class)
    public final static Capability<IFactionPlayerHandler> CAP = getNull();
    private final static String TAG = "FactionPlayerHandler";

    public static FactionPlayerHandler get(EntityPlayer player) {
        return (FactionPlayerHandler) player.getCapability(CAP, null);
    }


    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IFactionPlayerHandler.class, new Storage(), FactionPlayerHandlerDefaultImpl.class);
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityProvider createNewCapability(final EntityPlayer player) {
        return new ICapabilitySerializable<NBTTagCompound>() {

            final IFactionPlayerHandler inst = new FactionPlayerHandler(player);

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {

                return CAP.equals(capability) ? CAP.<T>cast(inst) : null;
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
                return CAP.equals(capability);
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final EntityPlayer player;
    private IPlayableFaction currentFaction = null;
    private int currentLevel = 0;

    private FactionPlayerHandler(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean canJoin(IPlayableFaction faction) {
        FactionEvent.CanJoinFaction event = new FactionEvent.CanJoinFaction(this, currentFaction, faction);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DEFAULT) {
            return currentFaction == null;
        }
        return event.getResult() == Event.Result.ALLOW;
    }

    @Override
    public boolean canLeaveFaction() {
        return currentFaction == null || currentFaction.getPlayerCapability(player).canLeaveFaction();
    }

    public void copyFrom(EntityPlayer old) {
        FactionPlayerHandler oldP = get(old);
        currentFaction = oldP.currentFaction;
        currentLevel = oldP.currentLevel;
        notifyFaction(oldP.currentFaction, oldP.currentLevel);
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.FACTION_PLAYER_HANDLER_KEY;
    }

    @Override
    public IPlayableFaction getCurrentFaction() {
        return currentFaction;
    }

    @Override
    public IFactionPlayer getCurrentFactionPlayer() {
        return currentFaction == null ? null : currentFaction.getPlayerCapability(player);
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public int getCurrentLevel(IPlayableFaction f) {
        return isInFaction(f) ? currentLevel : 0;
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public boolean isInFaction(@Nullable IPlayableFaction f) {
        return Objects.equals(currentFaction, f);
    }

    @Override
    public void joinFaction(@Nonnull IPlayableFaction faction) {
        if (canJoin(faction)) {
            setFactionAndLevel(faction, 1);
        }
    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        IPlayableFaction old = currentFaction;
        int oldLevel = currentLevel;
        String f = nbt.getString("faction");
        if ("null".equals(f)) {
            currentFaction = null;
            currentLevel = 0;
        } else {
            currentFaction = getFactionFromKey(new ResourceLocation(f));
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
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (Configs.pvp_only_between_factions && src instanceof EntityDamageSource) {
            if (src.getTrueSource() instanceof EntityPlayer) {
                FactionPlayerHandler other = get((EntityPlayer) src.getTrueSource());
                if (this.currentFaction != null && this.currentFaction.equals(other.currentFaction)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean setFactionAndLevel(IPlayableFaction faction, int level) {
        IPlayableFaction old = currentFaction;
        int oldLevel = currentLevel;
        if (currentFaction != null && (!currentFaction.equals(faction) || level == 0)) {
            if (!currentFaction.getPlayerCapability(player).canLeaveFaction()) {
                VampirismMod.log.i(TAG, "You cannot leave faction %s, it is prevented by respective mod", currentFaction.getKey());
                return false;
            }
        }
        if (faction != null && (level < 0 || level > faction.getHighestReachableLevel())) {
            VampirismMod.log.w(TAG, "Level %d in faction %s cannot be reached", level, faction.getKey());
            return false;
        }
        FactionEvent.ChangeLevelOrFaction event = new FactionEvent.ChangeLevelOrFaction(this, old, oldLevel, faction, faction == null ? 0 : level);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            VampirismMod.log.d(TAG, "Faction or Level change event canceled");
            return false;
        }
        if (faction == null) {
            currentFaction = null;
            currentLevel = 0;
        } else {
            currentFaction = faction;
            currentLevel = level;
        }
        if (currentFaction == null) currentLevel = 0;
        else if (currentLevel == 0) currentFaction = null;
        notifyFaction(old, oldLevel);
        sync(!Objects.equals(old, currentFaction));
        if (player instanceof EntityPlayerMP) {
            ModAdvancements.TRIGGER_FACTION.trigger((EntityPlayerMP) player, currentFaction, currentLevel);
        }
        return true;

    }

    @Override
    public boolean setFactionLevel(@Nonnull IPlayableFaction faction, int level) {
        return faction.equals(currentFaction) && setFactionAndLevel(faction, level);
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setString("faction", currentFaction == null ? "null" : currentFaction.getKey().toString());
        nbt.setInteger("level", currentLevel);
    }

    private IPlayableFaction getFactionFromKey(ResourceLocation key) {
        for (IPlayableFaction p : VampirismAPI.factionRegistry().getPlayableFactions()) {
            if (p.getKey().equals(key)) {
                return p;
            }
        }
        return null;
    }

    private void loadNBTData(NBTTagCompound nbt) {

        if (nbt.hasKey("faction")) {
            currentFaction = getFactionFromKey(new ResourceLocation(nbt.getString("faction")));
            if (currentFaction == null) {
                VampirismMod.log.w(TAG, "Could not find faction %s. Did mods change?", nbt.getString("faction"));
            } else {
                currentLevel = nbt.getInteger("level");
                notifyFaction(null, 0);
            }
        }
    }

    /**
     * Notify faction about changes.
     * {@link FactionPlayerHandler#currentFaction} and {@link FactionPlayerHandler#currentLevel} will be used as the new ones
     *
     * @param oldFaction
     * @param oldLevel
     */
    private void notifyFaction(IPlayableFaction oldFaction, int oldLevel) {
        if (oldFaction != null && !oldFaction.equals(currentFaction)) {
            VampirismMod.log.d(TAG, "Leaving faction %s", oldFaction.getKey());
            oldFaction.getPlayerCapability(player).onLevelChanged(0, oldLevel);
        }
        if (currentFaction != null) {
            VampirismMod.log.d(TAG, "Changing to %s %d", currentFaction, currentLevel);
            currentFaction.getPlayerCapability(player).onLevelChanged(currentLevel, Objects.equals(oldFaction, currentFaction) ? oldLevel : 0);
        }
        if (!Objects.equals(currentFaction, oldFaction)) {
            onChangedFaction();
        }
    }

    /**
     * Called when the faction has changed
     */
    private void onChangedFaction() {
        player.refreshDisplayName();
    }

    private void saveNBTData(NBTTagCompound nbt) {
        if (currentFaction != null) {
            nbt.setString("faction", currentFaction.getKey().toString());
            nbt.setInteger("level", currentLevel);
        }
    }

    private void sync(boolean all) {
        HelperLib.sync(this, player, all);
    }


    private static class Storage implements Capability.IStorage<IFactionPlayerHandler> {

        @Override
        public void readNBT(Capability<IFactionPlayerHandler> capability, IFactionPlayerHandler instance, EnumFacing side, NBTBase nbt) {
            ((FactionPlayerHandler) instance).loadNBTData((NBTTagCompound) nbt);
        }

        @Override
        public NBTBase writeNBT(Capability<IFactionPlayerHandler> capability, IFactionPlayerHandler instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            ((FactionPlayerHandler) instance).saveNBTData(nbt);
            return nbt;
        }
    }
}
