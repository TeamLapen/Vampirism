package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.event.FactionEvent;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler implements ISyncable.ISyncableEntityCapabilityInst, IFactionPlayerHandler {
    private final static Logger LOGGER = LogManager.getLogger(FactionPlayerHandler.class);
    @CapabilityInject(IFactionPlayerHandler.class)
    public static Capability<IFactionPlayerHandler> CAP = getNull();

    public static FactionPlayerHandler get(PlayerEntity player) {
        return (FactionPlayerHandler) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get FactionPlayerHandler from EntityPlayer " + player));
    }


    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IFactionPlayerHandler.class, new Storage(), FactionPlayerHandlerDefaultImpl::new);
    }

    public static ICapabilityProvider createNewCapability(final PlayerEntity player) {
        return new ICapabilitySerializable<CompoundNBT>() {

            final IFactionPlayerHandler inst = new FactionPlayerHandler(player);
            final LazyOptional<IFactionPlayerHandler> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {

                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundNBT serializeNBT() {
                return (CompoundNBT) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final PlayerEntity player;
    private IPlayableFaction currentFaction = null;
    private int currentLevel = 0;

    @Nullable
    private IAction boundAction1;
    @Nullable
    private IAction boundAction2;

    private FactionPlayerHandler(PlayerEntity player) {
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

    public void copyFrom(PlayerEntity old) {
        FactionPlayerHandler oldP = get(old);
        currentFaction = oldP.currentFaction;
        currentLevel = oldP.currentLevel;
        this.boundAction1 = oldP.boundAction1;
        this.boundAction2 = oldP.boundAction2;
        notifyFaction(oldP.currentFaction, oldP.currentLevel);
    }

    @Nullable
    public IAction getBoundAction1() {
        return boundAction1;
    }

    @Nullable
    public IAction getBoundAction2() {
        return boundAction2;
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
    public float getCurrentLevelRelative() {
        return currentFaction == null ? 0 : currentLevel / (float) currentFaction.getHighestReachableLevel();
    }

    @Override
    public PlayerEntity getPlayer() {
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
    public void loadUpdateFromNBT(CompoundNBT nbt) {
        IPlayableFaction old = currentFaction;
        int oldLevel = currentLevel;
        String f = nbt.getString("faction");
        if ("null".equals(f)) {
            currentFaction = null;
            currentLevel = 0;
        } else {
            currentFaction = getFactionFromKey(new ResourceLocation(f));
            if (currentFaction == null) {
                LOGGER.error("Cannot find faction {} on client. You have to register factions on both sides!", f);
                currentLevel = 0;
            } else {
                currentLevel = nbt.getInt("level");
            }
        }
        if (nbt.contains("bound1")) {
            setBoundAction1(ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound1"))), false);
        }
        if (nbt.contains("bound2")) {
            setBoundAction2(ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound2"))), false);
        }
        notifyFaction(old, oldLevel);
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (VampirismConfig.SERVER.pvpOnlyBetweenFactions.get() && src instanceof EntityDamageSource) {
            if (src.getTrueSource() instanceof PlayerEntity) {
                FactionPlayerHandler other = get((PlayerEntity) src.getTrueSource());
                return this.currentFaction == null || !this.currentFaction.equals(other.currentFaction);
            }
        }
        return true;
    }

    public void setBoundAction1(@Nullable IAction boundAction1, boolean sync) {
        this.boundAction1 = boundAction1;
        if (sync) {
            this.sync(false);
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.actions.bind_action", new TranslationTextComponent(boundAction1.getTranslationKey()).getFormattedText(), "1"), true);
        }
    }

    public void setBoundAction2(@Nullable IAction boundAction2, boolean sync) {
        this.boundAction2 = boundAction2;
        if (sync) {
            this.sync(false);
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.actions.bind_action", new TranslationTextComponent(boundAction2.getTranslationKey()).getFormattedText(), "2"), true);
        }
    }

    @Override
    public boolean setFactionAndLevel(IPlayableFaction faction, int level) {
        IPlayableFaction old = currentFaction;
        int oldLevel = currentLevel;
        if (currentFaction != null && (!currentFaction.equals(faction) || level == 0)) {
            if (!currentFaction.getPlayerCapability(player).canLeaveFaction()) {
                LOGGER.info("You cannot leave faction {}, it is prevented by respective mod", currentFaction.getID());
                return false;
            }
        }
        if (faction != null && (level < 0 || level > faction.getHighestReachableLevel())) {
            LOGGER.warn("Level {} in faction {} cannot be reached", level, faction.getID());
            return false;
        }
        FactionEvent.ChangeLevelOrFaction event = new FactionEvent.ChangeLevelOrFaction(this, old, oldLevel, faction, faction == null ? 0 : level);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            LOGGER.debug("Faction or Level change event canceled");
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
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_FACTION.trigger((ServerPlayerEntity) player, currentFaction, currentLevel);
        }
        return true;

    }

    @Override
    public boolean setFactionLevel(@Nonnull IPlayableFaction faction, int level) {
        return faction.equals(currentFaction) && setFactionAndLevel(faction, level);
    }

    @Override
    public void writeFullUpdateToNBT(CompoundNBT nbt) {
        nbt.putString("faction", currentFaction == null ? "null" : currentFaction.getID().toString());
        nbt.putInt("level", currentLevel);
        if (getBoundAction1() != null) nbt.putString("bound1", getBoundAction1().getRegistryName().toString());
        if (getBoundAction2() != null) nbt.putString("bound2", getBoundAction2().getRegistryName().toString());
    }

    private IPlayableFaction getFactionFromKey(ResourceLocation key) {
        for (IPlayableFaction p : VampirismAPI.factionRegistry().getPlayableFactions()) {
            if (p.getID().equals(key)) {
                return p;
            }
        }
        return null;
    }

    private void loadNBTData(CompoundNBT nbt) {
        if (nbt.contains("faction")) {
            currentFaction = getFactionFromKey(new ResourceLocation(nbt.getString("faction")));
            if (currentFaction == null) {
                LOGGER.warn("Could not find faction {}. Did mods change?", nbt.getString("faction"));
            } else {
                currentLevel = nbt.getInt("level");
                notifyFaction(null, 0);
            }
        }
        if (nbt.contains("bound1")) {
            LOGGER.info(new ResourceLocation(nbt.getString("bound1")));
            setBoundAction1(ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound1"))), false);
        }
        if (nbt.contains("bound2")) {
            LOGGER.info(new ResourceLocation(nbt.getString("bound1")));
            setBoundAction2(ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound2"))), false);
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
            LOGGER.debug("Leaving faction {}", oldFaction.getID());
            oldFaction.getPlayerCapability(player).onLevelChanged(0, oldLevel);
        }
        if (currentFaction != null) {
            LOGGER.debug("Changing to {} {}", currentFaction, currentLevel);
            currentFaction.getPlayerCapability(player).onLevelChanged(currentLevel, Objects.equals(oldFaction, currentFaction) ? oldLevel : 0);
        }
        ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.FACTION_CRITERIA, currentFaction == null ? 0 : currentFaction.getID().hashCode());
    }


    private void saveNBTData(CompoundNBT nbt) {
        //Don't forget to also add things to copyFrom
        if (currentFaction != null) {
            nbt.putString("faction", currentFaction.getID().toString());
            nbt.putInt("level", currentLevel);
        }
        if (getBoundAction1() != null) nbt.putString("bound1", getBoundAction1().getRegistryName().toString());
        if (getBoundAction2() != null) nbt.putString("bound2", getBoundAction2().getRegistryName().toString());
    }

    private void sync(boolean all) {
        HelperLib.sync(this, player, all);
    }


    private static class Storage implements Capability.IStorage<IFactionPlayerHandler> {

        @Override
        public void readNBT(Capability<IFactionPlayerHandler> capability, IFactionPlayerHandler instance, Direction side, INBT nbt) {
            ((FactionPlayerHandler) instance).loadNBTData((CompoundNBT) nbt);
        }

        @Override
        public INBT writeNBT(Capability<IFactionPlayerHandler> capability, IFactionPlayerHandler instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            ((FactionPlayerHandler) instance).saveNBTData(nbt);
            return nbt;
        }
    }
}
