package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.player.IVampirismPlayer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.tasks.reward.LordLevelReward;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.RegUtil;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import de.teamlapen.vampirism.world.MinionWorldData;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler implements ISyncable.ISyncableEntityCapabilityInst, IFactionPlayerHandler {
    private final static Logger LOGGER = LogManager.getLogger(FactionPlayerHandler.class);
    public static final Capability<IFactionPlayerHandler> CAP = CapabilityManager.get(new CapabilityToken<>(){});

    /**
     * Must check Entity#isAlive before
     * <br>
     * Always prefer using #getOpt
     */
    @Deprecated
    public static FactionPlayerHandler get(Player player) {
        return (FactionPlayerHandler) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get FactionPlayerHandler from EntityPlayer " + player));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<FactionPlayerHandler> getOpt(@Nonnull Player player) {
        LazyOptional<FactionPlayerHandler> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Faction player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    /**
     * Resolves the FactionPlayerHandler capability (prints a warning message if not present) and returns an Optional of the current IFactionPlayer instance
     */
    public static Optional<? extends IFactionPlayer<?>> getCurrentFactionPlayer(@Nonnull Player player){
        LazyOptional<FactionPlayerHandler> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Faction player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt.resolve().flatMap(FactionPlayerHandler::getCurrentFactionPlayer);
    }



    public static ICapabilityProvider createNewCapability(final Player player) {
        return new ICapabilitySerializable<CompoundTag>() {

            final FactionPlayerHandler inst = new FactionPlayerHandler(player);
            final LazyOptional<IFactionPlayerHandler> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                inst.loadNBTData(nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {

                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                inst.saveNBTData(tag);
                return tag;
            }
        };
    }

    private final Player player;
    @Nonnull
    private final Int2ObjectMap<IAction<?>> boundActions = new Int2ObjectArrayMap<>();
    private IPlayableFaction<?> currentFaction = null;
    private int currentLevel = 0;
    private int currentLordLevel = 0;
    /**
     * If true, use female version of lord titles
     * CAREFUL: Can be null before initialized
     */
    @Nullable
    private Boolean titleGender = null;

    private FactionPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public boolean canJoin(IPlayableFaction<?> faction) {
        Event.Result res = VampirismEventFactory.fireCanJoinFactionEvent(this, currentFaction, faction);
        if (res == Event.Result.DEFAULT) {
            return currentFaction == null;
        }
        return res == Event.Result.ALLOW;
    }

    @Override
    public boolean canLeaveFaction() {
        return currentFaction == null || currentFaction.getPlayerCapability(player).map(IFactionPlayer::canLeaveFaction).orElse(false);
    }

    /**
     * Make sure the player caps are valid (call #reviveCaps if dead)
     */
    public void copyFrom(Player old) {
        FactionPlayerHandler oldP = get(old);
        currentFaction = oldP.currentFaction;
        currentLevel = oldP.currentLevel;
        currentLordLevel = oldP.currentLordLevel;
        this.boundActions.putAll(oldP.boundActions);
        this.titleGender = oldP.titleGender;
        this.updateCache();
        notifyFaction(oldP.currentFaction, oldP.currentLevel);
    }

    /**
     * @param id ATM 1-3
     * @return action if bound
     */
    @Nullable
    public IAction<?> getBoundAction(int id) {
        return this.boundActions.get(id);
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.FACTION_PLAYER_HANDLER_KEY;
    }

    @Nullable
    @Override
    public IPlayableFaction<?> getCurrentFaction() {
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
    public int getCurrentLevel(IPlayableFaction<?> f) {
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
    public Component getLordTitle() {
        return currentLordLevel == 0 || currentFaction == null ? null : currentFaction.getLordTitle(currentLordLevel, titleGender != null && titleGender);
    }

    public int getMaxMinions() {
        return currentLordLevel * VampirismConfig.BALANCE.miMinionPerLordLevel.get();
    }

    @Nonnull
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getTheEntityID() {
        return player.getId();
    }

    @Override
    public boolean isInFaction(@Nullable IFaction<?> f) {
        return Objects.equals(currentFaction, f);
    }

    @Override
    public void joinFaction(@Nonnull IPlayableFaction<?> faction) {
        if (canJoin(faction)) {
            setFactionAndLevel(faction, 1);
        }
    }

    @Override
    public void loadUpdateFromNBT(CompoundTag nbt) {
        IPlayableFaction<?> old = currentFaction;
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
        if (old != currentFaction || oldLevel != currentLevel) {
            VampirismEventFactory.fireFactionLevelChangedEvent(this, old, oldLevel, currentFaction, currentLevel);
        }
        if (nbt.contains("title_gender")) {
            this.titleGender = nbt.getBoolean("title_gender");
        }
        this.loadBoundActions(nbt);
        updateCache();
        notifyFaction(old, oldLevel);
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (VampirismConfig.SERVER.pvpOnlyBetweenFactions.get() && src instanceof EntityDamageSource) {
            if (src.getEntity() instanceof Player) {
                IPlayableFaction<?> otherFaction = getOpt((Player) src.getEntity()).resolve().map(FactionPlayerHandler::getCurrentFaction).orElse(null);
                if (this.currentFaction == null || otherFaction == null) {
                    return VampirismConfig.SERVER.pvpOnlyBetweenFactionsIncludeHumans.get();
                }
                return !this.currentFaction.equals(otherFaction);
            }
        }
        return true;
    }

    /**
     * Must be called on player login
     */
    public void onPlayerLoggedIn() {
        if (this.titleGender == null) {
            this.titleGender = Helper.attemptToGuessGenderSafe(player);
        }
    }

    /**
     * Reset all lord task that should be available for players at the given lord level
     *
     * @param minLevel the lord level the player now has
     */
    public void resetLordTasks(int minLevel) {
        RegUtil.values(ModRegistries.TASKS).stream().filter(task -> task.isUnique() && task.getReward() instanceof LordLevelReward && ((LordLevelReward) task.getReward()).targetLevel > minLevel).forEach(task -> getCurrentFactionPlayer().map(IFactionPlayer::getTaskManager).ifPresent(manager -> manager.resetUniqueTask(task)));
    }

    public void setBoundAction(int id, @Nullable IAction<?> boundAction, boolean sync, boolean notify) {
        if (boundAction == null) {
            this.boundActions.remove(id);
        } else {
            this.boundActions.put(id, boundAction);
        }
        if (notify) {
            player.displayClientMessage(Component.translatable("text.vampirism.actions.bind_action", boundAction != null ? boundAction.getName() : "none", id), true);
        }
        if (sync) {
            this.sync(false);
        }
    }

    @Override
    public boolean setFactionAndLevel(@Nullable IPlayableFaction<?> faction, int level) {
        IPlayableFaction<?> old = currentFaction;
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
        if (VampirismEventFactory.fireChangeLevelOrFactionEvent(this, old, oldLevel, faction, faction == null ? 0 : level)) {
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
        updateCache();
        notifyFaction(old, oldLevel);
        if (old != currentFaction || oldLevel != currentLevel) {
            VampirismEventFactory.fireFactionLevelChangedEvent(this, old, oldLevel, currentFaction, currentLevel);
        }
        sync(!Objects.equals(old, currentFaction));
        if (player instanceof ServerPlayer) {
            ModAdvancements.TRIGGER_FACTION.trigger((ServerPlayer) player, currentFaction, currentLevel, currentLordLevel);
        }
        return true;

    }

    @Override
    public boolean setFactionLevel(@Nonnull IPlayableFaction<?> faction, int level) {
        return faction.equals(currentFaction) && setFactionAndLevel(faction, level);
    }

    @Override
    public boolean setLordLevel(int level) {
        return this.setLordLevel(level, true);
    }

    public boolean setTitleGender(boolean female) {
        if (titleGender == null || female != this.titleGender) {
            this.titleGender = female;
            player.refreshDisplayName();
            if (!player.level.isClientSide()) {
                sync(true);
            }
        }
        this.titleGender = female;
        return true;
    }

    @Override
    public void writeFullUpdateToNBT(CompoundTag nbt) {
        nbt.putString("faction", currentFaction == null ? "null" : currentFaction.getID().toString());
        nbt.putInt("level", currentLevel);
        nbt.putInt("lord_level", currentLordLevel);
        nbt.putBoolean("title_gender", titleGender != null && titleGender);
        this.writeBoundActions(nbt);
    }

    private IPlayableFaction<?> getFactionFromKey(ResourceLocation key) {
        for (IPlayableFaction<?> p : VampirismAPI.factionRegistry().getPlayableFactions()) {
            if (p.getID().equals(key)) {
                return p;
            }
        }
        return null;
    }

    private void loadBoundActions(CompoundTag nbt) {
        if (nbt.contains("bound1")) {
            this.boundActions.put(1, RegUtil.getAction(new ResourceLocation(nbt.getString("bound1"))));
        }
        if (nbt.contains("bound2")) {
            this.boundActions.put(2, RegUtil.getAction(new ResourceLocation(nbt.getString("bound2"))));
        }
        if (nbt.contains("bound3")) {
            this.boundActions.put(3, RegUtil.getAction(new ResourceLocation(nbt.getString("bound3"))));
        }
        CompoundTag bounds = nbt.getCompound("bound_actions");
        for (String s : bounds.getAllKeys()) {
            int id = Integer.parseInt(s);
            IAction<?> action = RegUtil.getAction(new ResourceLocation(bounds.getString(s)));
            this.boundActions.put(id, action);
        }
    }

    private void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("faction")) {
            currentFaction = getFactionFromKey(new ResourceLocation(nbt.getString("faction")));
            if (currentFaction == null) {
                LOGGER.warn("Could not find faction {}. Did mods change?", nbt.getString("faction"));
            } else {
                currentLevel = Math.min(nbt.getInt("level"), this.currentFaction.getHighestReachableLevel());
                currentLordLevel = Math.min(nbt.getInt("lord_level"), this.currentFaction.getHighestLordLevel());
                updateCache();
                notifyFaction(null, 0);
            }
        }
        if (nbt.contains("title_gender")) {
            this.titleGender = nbt.getBoolean("title_gender");
        }
        loadBoundActions(nbt);
        updateCache();
    }

    /**
     * Notify faction about changes.
     * {@link FactionPlayerHandler#currentFaction} and {@link FactionPlayerHandler#currentLevel} will be used as the new ones
     */
    private void notifyFaction(IPlayableFaction<?> oldFaction, int oldLevel) {
        if (oldFaction != null && !oldFaction.equals(currentFaction)) {
            LOGGER.debug(LogUtil.FACTION, "{} is leaving faction {}", this.player.getName().getString(), oldFaction.getID());
            oldFaction.getPlayerCapability(player).ifPresent(c -> c.onLevelChanged(0, oldLevel));
        }
        if (currentFaction != null) {
            LOGGER.debug(LogUtil.FACTION, "{} has new faction level {} {}", this.player.getName().getString(), currentFaction.getID(), currentLevel);
            currentFaction.getPlayerCapability(player).ifPresent(c -> c.onLevelChanged(currentLevel, Objects.equals(oldFaction, currentFaction) ? oldLevel : 0));
        }
        ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.FACTION_CRITERIA, currentFaction == null ? 0 : currentFaction.getID().hashCode());
    }

    private void saveNBTData(CompoundTag nbt) {
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

    private boolean setLordLevel(int level, boolean sync) {
        if (level > 0 && (currentFaction == null || currentLevel != currentFaction.getHighestReachableLevel() || level > currentFaction.getHighestLordLevel())) {
            return false;
        }
        if (level < this.currentLordLevel) {
            //down leveling -> Reset tasks
            resetLordTasks(level);
        }

        this.currentLordLevel = level;
        this.updateCache();
        MinionWorldData.getData(player.level).ifPresent(data -> {
            PlayerMinionController c = data.getController(this.player.getUUID());
            if (c != null) {
                c.setMaxMinions(this.currentFaction, this.getMaxMinions());
            }
        });
        if (level == 0) {
            LOGGER.debug(LogUtil.FACTION, "Resetting lord level for {}", this.player.getName());
        } else {
            LOGGER.debug(LogUtil.FACTION, "{} has now lord level {}", this.player.getName(), level);
        }
        if (player instanceof ServerPlayer) {
            ModAdvancements.TRIGGER_FACTION.trigger((ServerPlayer) player, currentFaction, currentLevel, currentLordLevel);
        }
        if (sync) sync(false);
        return true;
    }

    private void sync(boolean all) {
        HelperLib.sync(this, player, all);
    }

    private void updateCache() {
        player.refreshDisplayName();
        VampirismPlayerAttributes atts = ((IVampirismPlayer) player).getVampAtts();
        atts.hunterLevel = this.currentFaction == VReference.HUNTER_FACTION ? this.currentLevel : 0;
        atts.vampireLevel = this.currentFaction == VReference.VAMPIRE_FACTION ? this.currentLevel : 0;
        atts.lordLevel = this.currentLordLevel;
        atts.faction = this.currentFaction;
    }

    private void writeBoundActions(CompoundTag nbt) {
        CompoundTag bounds = new CompoundTag();
        for (Int2ObjectMap.Entry<IAction<?>> entry : this.boundActions.int2ObjectEntrySet()) {
            bounds.putString(String.valueOf(entry.getIntKey()), RegUtil.id(entry.getValue()).toString());
        }
        nbt.put("bound_actions", bounds);
    }
}
