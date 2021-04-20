package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.player.tasks.reward.LordLevelReward;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.ModEventFactory;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import de.teamlapen.vampirism.world.MinionWorldData;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler implements ISyncable.ISyncableEntityCapabilityInst, IFactionPlayerHandler {
    private final static Logger LOGGER = LogManager.getLogger(FactionPlayerHandler.class);
    @CapabilityInject(IFactionPlayerHandler.class)
    public static Capability<IFactionPlayerHandler> CAP = getNull();

    /**
     * Must check Entity#isAlive before
     */
    public static FactionPlayerHandler get(PlayerEntity player) {
        return (FactionPlayerHandler) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get FactionPlayerHandler from EntityPlayer " + player));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<FactionPlayerHandler> getOpt(@Nonnull PlayerEntity player) {
        LazyOptional<FactionPlayerHandler> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Faction player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
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
    private IPlayableFaction<? extends IFactionPlayer<?>> currentFaction = null;
    private int currentLevel = 0;
    private int currentLordLevel = 0;
    /**
     * If true, use female version of lord titles
     * CAREFUL: Can be null before initialized
     */
    @Nullable
    private Boolean titleGender = null;

    @Nonnull
    private final Int2ObjectMap<IAction> boundActions = new Int2ObjectArrayMap<>();

    private FactionPlayerHandler(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean canJoin(IPlayableFaction<? extends IFactionPlayer<?>> faction) {
        Event.Result res = ModEventFactory.fireCanJoinFactionEvent(this, currentFaction, faction);
        if (res == Event.Result.DEFAULT) {
            return currentFaction == null;
        }
        return res == Event.Result.ALLOW;
    }

    @Override
    public boolean canLeaveFaction() {
        return currentFaction == null || currentFaction.getPlayerCapability(player).map(IFactionPlayer::canLeaveFaction).orElse(false);
    }

    public void copyFrom(PlayerEntity old) {
        FactionPlayerHandler oldP = get(old);
        currentFaction = oldP.currentFaction;
        currentLevel = oldP.currentLevel;
        currentLordLevel = oldP.currentLordLevel;
        this.boundActions.putAll(oldP.boundActions);
        this.titleGender = oldP.titleGender;
        notifyFaction(oldP.currentFaction, oldP.currentLevel);
    }

    /**
     * @see #getBoundAction(int)
     */
    @Deprecated
    @Nullable
    public IAction getBoundAction1() { //TODO 1.17 remove
        return getBoundAction(1);
    }

    /**
     * @see #getBoundAction(int)
     */
    @Deprecated
    @Nullable
    public IAction getBoundAction2() { //TODO 1.17 remove
        return getBoundAction(2);
    }

    @Nullable
    public IAction getBoundAction(int id) {
        return this.boundActions.get(id);
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.FACTION_PLAYER_HANDLER_KEY;
    }

    @Nullable
    @Override
    public IPlayableFaction<? extends IFactionPlayer<?>> getCurrentFaction() {
        return currentFaction;
    }

    @Nonnull
    @Override
    public Optional<? extends IFactionPlayer<?>> getCurrentFactionPlayer() {
        return currentFaction == null ? Optional.empty() : currentFaction.getPlayerCapability(player).map(Optional::of).orElse(Optional.empty());
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public int getCurrentLevel(IPlayableFaction<? extends IFactionPlayer<?>> f) {
        return isInFaction(f) ? currentLevel : 0;
    }

    @Override
    public float getCurrentLevelRelative() {
        return currentFaction == null ? 0 : currentLevel / (float) currentFaction.getHighestReachableLevel();
    }

    @Nullable
    @Override
    public IPlayableFaction<?> getLordFaction() {
        return currentLordLevel > 0 ? currentFaction : null;
    }

    @Override
    public int getLordLevel() {
        return currentLordLevel;
    }

    @Nullable
    @Override
    public ITextComponent getLordTitle() {
        return currentLordLevel == 0 || currentFaction == null ? null : currentFaction.getLordTitle(currentLordLevel, titleGender != null && titleGender);
    }

    @Nonnull
    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public boolean isInFaction(@Nullable IPlayableFaction<? extends IFactionPlayer<?>> f) {
        return Objects.equals(currentFaction, f);
    }

    @Override
    public void joinFaction(@Nonnull IPlayableFaction<? extends IFactionPlayer<?>> faction) {
        if (canJoin(faction)) {
            setFactionAndLevel(faction, 1);
        }
    }

    /**
     * Must be called on player login
     */
    public void onPlayerLoggedIn() {
        if (this.titleGender == null) {
            this.titleGender = Helper.attemptToGuessGenderSafe(player);
        }
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

    /**
     * @see #setBoundAction(int, IAction, boolean, boolean)
     */
    @Deprecated
    public void setBoundAction1(@Nullable IAction boundAction1, boolean sync) { //TODO 1.17 remove
        this.setBoundAction(1, boundAction1, sync, true);
    }

    /**
     * @see #setBoundAction(int, IAction, boolean, boolean)
     */
    @Deprecated
    public void setBoundAction2(@Nullable IAction boundAction2, boolean sync) { //TODO 1.17 remove
        this.setBoundAction(2, boundAction2, sync, true);
    }

    public void setBoundAction(int id, @Nullable IAction boundAction, boolean sync, boolean notify) {
        if (boundAction == null) {
            this.boundActions.remove(id);
        } else {
            this.boundActions.put(id, boundAction);
        }
        if (notify) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.actions.bind_action", boundAction != null ? boundAction.getName() : "none", id), true);
        }
        if (sync) {
            this.sync(false);
        }
    }

    @Override
    public boolean setFactionAndLevel(@Nullable IPlayableFaction<? extends IFactionPlayer<?>> faction, int level) {
        IPlayableFaction<? extends IFactionPlayer<?>> old = currentFaction;
        int oldLevel = currentLevel;
        int newLordLevel = this.currentLordLevel;

        if (currentFaction != null && (!currentFaction.equals(faction) || level == 0)) {
            if (!currentFaction.getPlayerCapability(player).map(IFactionPlayer::canLeaveFaction).orElse(false)) {
                LOGGER.info("You cannot leave faction {}, it is prevented by respective mod", currentFaction.getID());
                return false;
            }
        }
        if (faction != null && (level < 0 || level > faction.getHighestReachableLevel())) {
            LOGGER.warn("Level {} in faction {} cannot be reached", level, faction.getID());
            return false;
        }
        if (ModEventFactory.fireChangeLevelOrFactionEvent(this, old, oldLevel, faction, faction == null ? 0 : level)) {
            LOGGER.debug("Faction or Level change event canceled");
            return false;
        }
        if (this.currentFaction != null && faction != this.currentFaction) {
            this.currentFaction.getPlayerCapability(player).ifPresent(factionPlayer -> factionPlayer.getTaskManager().reset());
        }
        if (faction == null) {
            currentFaction = null;
            currentLevel = 0;
            newLordLevel = 0;
        } else {
            currentFaction = faction;
            currentLevel = level;
            if (currentLevel != currentFaction.getHighestReachableLevel() || currentFaction != old) {
                newLordLevel = 0;
            }
        }
        if (currentLevel == 0) {
            currentFaction = null;
            newLordLevel = 0;
        }
        if (currentLordLevel != newLordLevel) {
            this.setLordLevel(newLordLevel, false);
        }
        notifyFaction(old, oldLevel);
//        if(faction != null && faction != old) {
//            faction.getPlayerCapability(player).ifPresent(factionPlayer -> factionPlayer.getTaskManager().init());
//        }
        sync(!Objects.equals(old, currentFaction));
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_FACTION.trigger((ServerPlayerEntity) player, currentFaction, currentLevel, currentLordLevel);
        }
        player.refreshDisplayName();
        return true;

    }

    @Override
    public boolean setLordLevel(int level) {
        return this.setLordLevel(level, true);
    }

    /**
     * Reset all lord task that should be available for players at the given lord level
     *
     * @param minLevel the lord level the player now has
     */
    public void resetLordTasks(int minLevel) {
        ModRegistries.TASKS.getValues().stream().filter(task -> task.isUnique() && task.getReward() instanceof LordLevelReward && ((LordLevelReward) task.getReward()).targetLevel > minLevel).forEach(task -> getCurrentFactionPlayer().map(IFactionPlayer::getTaskManager).ifPresent(manager -> manager.resetUniqueTask(task)));
    }

    private boolean setLordLevel(int level, boolean sync) {
        if (level > 0 && (currentFaction == null || currentLevel != currentFaction.getHighestReachableLevel() || level > currentFaction.getHighestLordLevel())) {
            return false;
        }
        if (level < this.currentLordLevel) {
            //Downleveling -> Reset tasks
            resetLordTasks(level);
        }

        this.currentLordLevel = level;
        MinionWorldData.getData(player.world).ifPresent(data -> {
            PlayerMinionController c = data.getController(this.player.getUniqueID());
            if (c != null) {
                c.setMaxMinions(this.currentFaction, this.getMaxMinions());
            }
        });
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_FACTION.trigger((ServerPlayerEntity) player, currentFaction, currentLevel, currentLordLevel);
        }
        if (sync) sync(false);
        player.refreshDisplayName();
        return true;
    }

    public boolean setTitleGender(boolean female) {
        if (titleGender == null || female != this.titleGender) {
            this.titleGender = female;
            player.refreshDisplayName();
            if (!player.world.isRemote()) {
                sync(true);
            }
        }
        this.titleGender = female;
        return true;
    }

    @Override
    public boolean setFactionLevel(@Nonnull IPlayableFaction<? extends IFactionPlayer<?>> faction, int level) {
        return faction.equals(currentFaction) && setFactionAndLevel(faction, level);
    }

    @Override
    public void writeFullUpdateToNBT(CompoundNBT nbt) {
        nbt.putString("faction", currentFaction == null ? "null" : currentFaction.getID().toString());
        nbt.putInt("level", currentLevel);
        nbt.putInt("lord_level", currentLordLevel);
        nbt.putBoolean("title_gender", titleGender != null && titleGender);
        this.writeBoundActions(nbt);
    }

    @Override
    public void loadUpdateFromNBT(CompoundNBT nbt) {
        IPlayableFaction<? extends IFactionPlayer<?>> old = currentFaction;
        int oldLevel = currentLevel;
        String f = nbt.getString("faction");
        if ("null".equals(f)) {
            currentFaction = null;
            currentLevel = 0;
            currentLordLevel = 0;
        } else {
            currentFaction = getFactionFromKey(new ResourceLocation(f));
            if (currentFaction == null) {
                LOGGER.error("Cannot find faction {} on client. You have to register factions on both sides!", f);
                currentLevel = 0;
            } else {
                currentLevel = nbt.getInt("level");
                currentLordLevel = nbt.getInt("lord_level");
            }
        }
        if (nbt.contains("title_gender")) {
            this.titleGender = nbt.getBoolean("title_gender");
        }
        this.loadBoundActions(nbt);
        notifyFaction(old, oldLevel);
    }

    private void saveNBTData(CompoundNBT nbt) {
        //Don't forget to also add things to copyFrom !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (currentFaction != null) {
            nbt.putString("faction", currentFaction.getID().toString());
            nbt.putInt("level", currentLevel);
            nbt.putInt("lord_level", currentLordLevel);
        }
        if (titleGender != null) nbt.putBoolean("title_gender", titleGender);

        writeBoundActions(nbt);
        //Don't forget to also add things to copyFrom !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    private void loadNBTData(CompoundNBT nbt) {
        if (nbt.contains("faction")) {
            currentFaction = getFactionFromKey(new ResourceLocation(nbt.getString("faction")));
            if (currentFaction == null) {
                LOGGER.warn("Could not find faction {}. Did mods change?", nbt.getString("faction"));
            } else {
                currentLevel = nbt.getInt("level");
                currentLordLevel = nbt.getInt("lord_level");
                notifyFaction(null, 0);
            }
        }
        if (nbt.contains("title_gender")) {
            this.titleGender = nbt.getBoolean("title_gender");
        }
        loadBoundActions(nbt);
    }

    private void writeBoundActions(CompoundNBT nbt) {
        CompoundNBT bounds = new CompoundNBT();
        for (Int2ObjectMap.Entry<IAction> entry : this.boundActions.int2ObjectEntrySet()) {
            bounds.putString(String.valueOf(entry.getIntKey()), entry.getValue().getRegistryName().toString());
        }
        nbt.put("bound_actions", bounds);
    }

    private void loadBoundActions(CompoundNBT nbt) {
        if (nbt.contains("bound1")) {
            this.boundActions.put(1, ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound1"))));
        }
        if (nbt.contains("bound2")) {
            this.boundActions.put(2, ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound2"))));
        }
        if (nbt.contains("bound3")) {
            this.boundActions.put(3, ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound3"))));
        }
        CompoundNBT bounds = nbt.getCompound("bound_actions");
        for (String s : bounds.keySet()) {
            int id = Integer.parseInt(s);
            IAction action = ModRegistries.ACTIONS.getValue(new ResourceLocation(bounds.getString(s)));
            this.boundActions.put(id, action);
        }
    }

    private IPlayableFaction<? extends IFactionPlayer<?>> getFactionFromKey(ResourceLocation key) {
        for (IPlayableFaction p : VampirismAPI.factionRegistry().getPlayableFactions()) {
            if (p.getID().equals(key)) {
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
    private void notifyFaction(IPlayableFaction<? extends IFactionPlayer<?>> oldFaction, int oldLevel) {
        if (oldFaction != null && !oldFaction.equals(currentFaction)) {
            LOGGER.debug("Leaving faction {}", oldFaction.getID());
            oldFaction.getPlayerCapability(player).ifPresent(c -> c.onLevelChanged(0, oldLevel));
        }
        if (currentFaction != null) {
            LOGGER.debug("Changing to {} {}", currentFaction, currentLevel);
            currentFaction.getPlayerCapability(player).ifPresent(c -> c.onLevelChanged(currentLevel, Objects.equals(oldFaction, currentFaction) ? oldLevel : 0));
        }
        ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.FACTION_CRITERIA, currentFaction == null ? 0 : currentFaction.getID().hashCode());
    }


    private void sync(boolean all) {
        HelperLib.sync(this, player, all);
    }


    public int getMaxMinions() {
        return currentLordLevel * VampirismConfig.BALANCE.miMinionPerLordLevel.get();
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
