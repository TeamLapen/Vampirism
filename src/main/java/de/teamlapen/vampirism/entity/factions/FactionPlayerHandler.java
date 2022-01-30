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
import de.teamlapen.vampirism.util.ScoreboardUtil;
import de.teamlapen.vampirism.util.VampirismEventFactory;
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
     * You should really use #getOpt instead
     * Must check Entity#isAlive before
     */
    @Deprecated
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

    public static Optional<? extends IFactionPlayer<?>> getCurrentFactionPlayer(@Nonnull PlayerEntity player){
        LazyOptional<FactionPlayerHandler> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Faction player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt.resolve().flatMap(FactionPlayerHandler::getCurrentFactionPlayer);
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
    @Nonnull
    private final Int2ObjectMap<IAction> boundActions = new Int2ObjectArrayMap<>();
    private IPlayableFaction<? extends IFactionPlayer<?>> currentFaction = null;
    private int currentLevel = 0;
    private int currentLordLevel = 0;
    /**
     * If true, use female version of lord titles
     * CAREFUL: Can be null before initialized
     */
    @Nullable
    private Boolean titleGender = null;

    private FactionPlayerHandler(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean canJoin(IPlayableFaction<? extends IFactionPlayer<?>> faction) {
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

    public void copyFrom(PlayerEntity old) {
        FactionPlayerHandler oldP = get(old);
        currentFaction = oldP.currentFaction;
        currentLevel = oldP.currentLevel;
        currentLordLevel = oldP.currentLordLevel;
        this.boundActions.putAll(oldP.boundActions);
        this.titleGender = oldP.titleGender;
        this.updateCache();
        notifyFaction(oldP.currentFaction, oldP.currentLevel);
    }

    @Nullable
    public IAction getBoundAction(int id) {
        return this.boundActions.get(id);
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
        return currentFaction == null ? Optional.empty() : currentFaction.getPlayerCapability(player).map(Optional::of).orElseGet(Optional::empty);
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

    public int getMaxMinions() {
        return currentLordLevel * VampirismConfig.BALANCE.miMinionPerLordLevel.get();
    }

    @Nonnull
    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public int getTheEntityID() {
        return player.getId();
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
        if(old != currentFaction || oldLevel != currentLevel){
            VampirismEventFactory.fireFactionLevelChangedEvent(this,old,oldLevel,currentFaction,currentLevel);
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
            if (src.getEntity() instanceof PlayerEntity) {
                IPlayableFaction<?> otherFaction = getOpt((PlayerEntity) src.getEntity()).resolve().map(FactionPlayerHandler::getCurrentFaction).orElse(null);
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
        ModRegistries.TASKS.getValues().stream().filter(task -> task.isUnique() && task.getReward() instanceof LordLevelReward && ((LordLevelReward) task.getReward()).targetLevel > minLevel).forEach(task -> getCurrentFactionPlayer().map(IFactionPlayer::getTaskManager).ifPresent(manager -> manager.resetUniqueTask(task)));
    }

    public void setBoundAction(int id, @Nullable IAction boundAction, boolean sync, boolean notify) {
        if (boundAction == null) {
            this.boundActions.remove(id);
        } else {
            this.boundActions.put(id, boundAction);
        }
        if (notify) {
            player.displayClientMessage(new TranslationTextComponent("text.vampirism.actions.bind_action", boundAction != null ? boundAction.getName() : "none", id), true);
        }
        if (sync) {
            this.sync(false);
        }
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
        if(old != currentFaction || oldLevel != currentLevel){
            VampirismEventFactory.fireFactionLevelChangedEvent(this,old,oldLevel,currentFaction,currentLevel);
        }
        sync(!Objects.equals(old, currentFaction));
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_FACTION.trigger((ServerPlayerEntity) player, currentFaction, currentLevel, currentLordLevel);
        }
        return true;

    }

    @Override
    public boolean setFactionLevel(@Nonnull IPlayableFaction<? extends IFactionPlayer<?>> faction, int level) {
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

    public boolean getTitleGender() {
        return this.titleGender;
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
    public void leaveFaction(boolean die) {
        IFaction<?> oldFaction = currentFaction;
        if(oldFaction==null)return;
        setFactionAndLevel(null, 0);
        player.displayClientMessage(new TranslationTextComponent("command.vampirism.base.level.successful", player.getName(), oldFaction.getName(), 0), true);
        if (die) {
            player.hurt(DamageSource.MAGIC, 1000);
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

    private void loadBoundActions(CompoundNBT nbt) {
        // Read bound actions from legacy format
        if (nbt.contains("bound1")) {
            IAction i = ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound1")));
            if(i!=null)this.boundActions.put(1,i);
        }
        if (nbt.contains("bound2")) {
            IAction i = ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound2")));
            if(i!=null)this.boundActions.put(2,i);
        }
        if (nbt.contains("bound3")) {
            IAction i = ModRegistries.ACTIONS.getValue(new ResourceLocation(nbt.getString("bound3")));
            if(i!=null)this.boundActions.put(3,i);
        }
        // Read bound actions from new format
        CompoundNBT bounds = nbt.getCompound("bound_actions");
        for (String s : bounds.getAllKeys()) {
            int id = Integer.parseInt(s);
            IAction action = ModRegistries.ACTIONS.getValue(new ResourceLocation(bounds.getString(s)));
            if(action == null){
                LOGGER.warn("Cannot find bound action {}", bounds.getString(s));
            }
            else{
                this.boundActions.put(id, action);
            }
        }
    }

    private void loadNBTData(CompoundNBT nbt) {
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
     *
     * @param oldFaction
     * @param oldLevel
     */
    private void notifyFaction(IPlayableFaction<? extends IFactionPlayer<?>> oldFaction, int oldLevel) {
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
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_FACTION.trigger((ServerPlayerEntity) player, currentFaction, currentLevel, currentLordLevel);
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

    private void writeBoundActions(CompoundNBT nbt) {
        CompoundNBT bounds = new CompoundNBT();
        for (Int2ObjectMap.Entry<IAction> entry : this.boundActions.int2ObjectEntrySet()) {
            bounds.putString(String.valueOf(entry.getIntKey()), entry.getValue().getRegistryName().toString());
        }
        nbt.put("bound_actions", bounds);
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
