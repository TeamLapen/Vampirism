package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.storage.IAttachment;
import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.advancements.critereon.FactionCriterionTrigger;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.*;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.core.tags.ModTaskTags;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.misc.VampirismLogger;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.RegUtil;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import de.teamlapen.vampirism.world.MinionWorldData;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler implements IAttachment, IFactionPlayerHandler {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final String NBT_KEY = "faction_player_handler";
    public static final ResourceLocation SERIALIZER_ID = VResourceLocation.mod(NBT_KEY);

    public static @NotNull FactionPlayerHandler get(@NotNull Player player) {
        return player.getData(ModAttachments.FACTION_PLAYER_HANDLER.get());
    }

    /**
     * @deprecated a player will always have a FactionPlayerHandler
     */
    @Deprecated
    public static @NotNull Optional<FactionPlayerHandler> getOpt(@NotNull Player player) {
        return Optional.of(player.getData(ModAttachments.FACTION_PLAYER_HANDLER.get()));
    }

    /**
     * Resolves the FactionPlayerHandler capability (prints a warning message if not present) and returns an Optional of the current IFactionPlayer instance
     */
    public static <T extends IFactionPlayer<T>> @NotNull Optional<T> getCurrentFactionPlayer(@NotNull Player player) {
        return get(player).getCurrentFactionPlayer();
    }

    private final Player player;
    @NotNull
    private final Int2ObjectMap<Holder<IAction<?>>> boundActions = new Int2ObjectArrayMap<>();
    private @Nullable Holder<? extends IPlayableFaction<?>> currentFaction = null;
    private int currentLevel = 0;
    private int currentLordLevel = 0;
    @NotNull
    private IPlayableFaction.TitleGender titleGender = IPlayableFaction.TitleGender.UNKNOWN;

    public FactionPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Player asEntity() {
        return player;
    }

    @Override
    public boolean canJoin(Holder<? extends IPlayableFaction<?>> faction) {
        PlayerFactionEvent.CanJoinFaction.Behavior behavior = VampirismEventFactory.fireCanJoinFactionEvent(this, currentFaction, faction);
        if (behavior == PlayerFactionEvent.CanJoinFaction.Behavior.ONLY_WHEN_NO_FACTION) {
            return currentFaction == null;
        }
        return behavior == PlayerFactionEvent.CanJoinFaction.Behavior.ALLOW;
    }

    @Override
    public boolean canJoin(IPlayableFaction<?> faction) {
        return canJoin(RegUtil.holder(faction));
    }

    @Override
    public boolean canLeaveFaction() {
        return currentFaction == null || currentFaction.value().getPlayerCapability(player).map(IFactionPlayer::canLeaveFaction).orElse(false);
    }

    /**
     * @param id ATM 1-3
     * @return action if bound
     */
    @Nullable
    public Holder<IAction<?>> getBoundAction(int id) {
        return this.boundActions.get(id);
    }

    @Override
    public @NotNull ResourceLocation getAttachedKey() {
        return SERIALIZER_ID;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    @Deprecated
    public <T extends IFactionPlayer<T>> IPlayableFaction<T> getCurrentFaction() {
        return currentFaction == null ? null : (IPlayableFaction<T>) currentFaction.value();
    }

    @Nullable
    @Override
    public Holder<? extends IPlayableFaction<?>> getFaction() {
        return currentFaction;
    }

    @NotNull
    @Override
    public <T extends IFactionPlayer<T>> Optional<T> getCurrentFactionPlayer() {
        return currentFaction == null ? Optional.empty() : (Optional<T>) currentFaction.value().getPlayerCapability(player);
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
    public int getCurrentLevel(Holder<? extends IPlayableFaction<?>> f) {
        return isInFaction(f) ? currentLevel : 0;
    }

    @Override
    public float getCurrentLevelRelative() {
        return currentFaction == null ? 0 : currentLevel / (float) currentFaction.value().getHighestReachableLevel();
    }

    @Override
    public @NotNull Optional<Holder<? extends IPlayableFaction<?>>> getLordFaction() {
        return currentLordLevel > 0 ? Optional.ofNullable(currentFaction) : Optional.empty();
    }

    @Override
    public int getLordLevel() {
        return currentLordLevel;
    }

    @Nullable
    @Override
    public Component getLordTitle() {
        return lordTitles().map(titles -> titles.getLordTitle(currentLordLevel, titleGender)).orElse(null);
    }

    @Override
    public @Nullable Component getLordTitleShort() {
        return lordTitles().map(titles -> titles.getShort(currentLordLevel, titleGender)).orElse(null);
    }

    public @NotNull Optional<ILordTitleProvider> lordTitles() {
        return Optional.ofNullable(currentFaction).map(Holder::value).map(IPlayableFaction::lordTiles);
    }

    public int getMaxMinions() {
        return currentLordLevel * VampirismConfig.BALANCE.miMinionPerLordLevel.get();
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public IPlayableFaction.TitleGender titleGender() {
        return this.titleGender;
    }

    @Override
    public boolean isInFaction(@Nullable IFaction<?> f) {
        if (this.currentFaction != null) {
            return Objects.equals(currentFaction.value(), f);
        }
        return Objects.equals(null, f);
    }

    @Override
    public <T extends IFaction<?>>  boolean isInFaction(@Nullable Holder<T> f) {
        return IFaction.is(currentFaction, f);
    }

    @Override
    public void joinFaction(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        if (canJoin(faction)) {
            setFactionAndLevel(faction, 1);
        }
    }

    @Override
    public void joinFaction(@NotNull IPlayableFaction<?> faction) {
        joinFaction(RegUtil.holder(faction));
    }

    @Override
    public void deserializeUpdateNBT(HolderLookup.Provider provider, @NotNull CompoundTag nbt) {
        Holder<? extends IPlayableFaction<?>> old = currentFaction;
        int oldLevel = currentLevel;
        if (nbt.contains("faction", Tag.TAG_STRING)) {
            String f = nbt.getString("faction");
            if ("null".equals(f)) {
                currentFaction = null;
                currentLevel = 0;
                currentLordLevel = 0;
            } else {
                currentFaction = getFactionFromKey(ResourceLocation.parse(f));
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
        }
        if (nbt.contains("title_gender", Tag.TAG_STRING)) {
            this.titleGender = IPlayableFaction.TitleGender.valueOf(nbt.getString("title_gender"));
        }
        this.loadBoundActions(nbt);
        updateCache();
        notifyFaction(old, oldLevel);
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (VampirismConfig.SERVER.pvpOnlyBetweenFactions.get() && src.getEntity() instanceof Player) {
            Holder<? extends IPlayableFaction<?>> otherFaction = get((Player) src.getEntity()).getFaction();
            if (this.currentFaction == null || otherFaction == null) {
                return VampirismConfig.SERVER.pvpOnlyBetweenFactionsIncludeHumans.get();
            }
            return !this.currentFaction.equals(otherFaction);
        }
        return true;
    }

    /**
     * Reset all lord task that should be available for players at the given lord level
     *
     * @param minLevel the lord level the player now has
     */
    public void resetLordTasks(int minLevel) {
        getCurrentFactionPlayer().map(IFactionPlayer::getTaskManager).ifPresent(manager -> {
            this.player.level().registryAccess().registryOrThrow(VampirismRegistries.Keys.TASK).getTagOrEmpty(ModTaskTags.AWARDS_LORD_LEVEL).forEach(holder -> {
                holder.unwrapKey().ifPresent(manager::resetUniqueTask);
            });
        });
    }

    public void setBoundAction(int id, @Nullable Holder<IAction<?>> boundAction, boolean sync, boolean notify) {
        if (boundAction == null) {
            this.boundActions.remove(id);
        } else {
            this.boundActions.put(id, boundAction);
        }
        if (notify) {
            player.displayClientMessage(Component.translatable("text.vampirism.actions.bind_action", boundAction != null ? boundAction.value().getName() : "none", id), true);
        }
        if (sync) {
            this.sync(false);
        }
    }

    @Override
    public boolean setFactionAndLevel(@Nullable Holder<? extends IPlayableFaction<?>> faction, int level) {
        Holder<? extends IPlayableFaction<?>> old = currentFaction;
        int oldLevel = currentLevel;
        int newLordLevel = this.currentLordLevel;

        if (currentFaction != null && (!currentFaction.equals(faction) || level == 0)) {
            if (!currentFaction.value().getPlayerCapability(player).map(IFactionPlayer::canLeaveFaction).orElse(false)) {
                LOGGER.info("You cannot leave faction {}, it is prevented by respective mod", currentFaction.getRegisteredName());
                return false;
            }
        }
        if (faction != null && (level < 0 || level > faction.value().getHighestReachableLevel())) {
            LOGGER.warn("Level {} in faction {} cannot be reached", level, faction.getRegisteredName());
            return false;
        }
        if (VampirismEventFactory.fireChangeLevelOrFactionEvent(this, old, oldLevel, faction, faction == null ? 0 : level)) {
            LOGGER.debug("Faction or Level change event canceled");
            return false;
        }
        if (this.currentFaction != null && faction != this.currentFaction) {
            this.currentFaction.value().getPlayerCapability(player).ifPresent(factionPlayer -> factionPlayer.getTaskManager().reset());
        }
        if (faction == null) {
            currentFaction = null;
            currentLevel = 0;
            newLordLevel = 0;
        } else {
            currentFaction = faction;
            currentLevel = level;
            if (currentLevel != currentFaction.value().getHighestReachableLevel() || currentFaction != old) {
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
        this.checkSkillTreeLocks();
        updateCache();
        notifyFaction(old, oldLevel);
        if (this.player instanceof ServerPlayer && !(currentFaction == old && oldLevel == currentLevel)) {
            if (old == currentFaction) {
                VampirismLogger.info(VampirismLogger.LEVEL, "{} has new faction level {} {}, was {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel, oldLevel);
            } else if (currentFaction != null) {
                VampirismLogger.info(VampirismLogger.LEVEL, "{} is now in faction {} {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel);
            } else {
                VampirismLogger.info(VampirismLogger.LEVEL, "{} has now no level", this.player.getName().getString());
            }
        }
        if(old != currentFaction || oldLevel != currentLevel){
            VampirismEventFactory.fireFactionLevelChangedEvent(this,old,oldLevel,currentFaction,currentLevel);
        }
        sync(!Objects.equals(old, currentFaction));
        if (player instanceof ServerPlayer serverPlayer) {
            if (old != faction) {
                ModAdvancements.TRIGGER_FACTION.get().revokeAll(serverPlayer);
                ModAdvancements.revoke(ModAdvancements.TRIGGER_MOTHER_WIN.get(), serverPlayer);
            } else if (oldLevel > level) {
                ModAdvancements.TRIGGER_FACTION.get().revokeLevel(serverPlayer, faction, FactionCriterionTrigger.Type.LEVEL, level);
            }
            ModAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
        }
        return true;

    }

    @Override
    public boolean setFactionAndLevel(@Nullable IPlayableFaction<?> faction, int level) {
        return setFactionAndLevel(faction == null ? null : RegUtil.holder(faction), level);
    }

    @Override
    public boolean setFactionLevel(@NotNull Holder<? extends IPlayableFaction<?>> faction, int level) {
        return faction == currentFaction && setFactionAndLevel(faction, level);
    }

    @Override
    public boolean setFactionLevel(@NotNull IPlayableFaction<?> faction, int level) {
        return setFactionLevel(RegUtil.holder(faction), level);
    }

    @Override
    public boolean setLordLevel(int level) {
        return this.setLordLevel(level, true);
    }

    public boolean setTitleGender(boolean female) {
        this.titleGender = female ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE;
            player.refreshDisplayName();
            if (!player.level().isClientSide()) {
                sync(true);
            }
        return true;
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("faction", Optional.ofNullable(this.currentFaction).flatMap(Holder::unwrapKey).map(ResourceKey::location).map(ResourceLocation::toString).orElse("null"));
        nbt.putInt("level", currentLevel);
        nbt.putInt("lord_level", currentLordLevel);
        nbt.putString("title_gender", titleGender.name());
        this.writeBoundActions(nbt);
        return nbt;
    }

    @Override
    public void leaveFaction(boolean die) {
        Holder<? extends IFaction<?>> oldFaction = currentFaction;
        if (oldFaction == null) return;
        setFactionAndLevel((Holder<IPlayableFaction<?>>) null, 0);
        player.displayClientMessage(Component.translatable("command.vampirism.base.level.successful", player.getName(), oldFaction.value().getName(), 0), true);
        if (die) {
            DamageHandler.kill(player, 10000);
        }
    }

    private @Nullable Holder<? extends IPlayableFaction<?>> getFactionFromKey(ResourceLocation key) {
        return (Holder<? extends IPlayableFaction<?>>) ModRegistries.FACTIONS.getHolder(key).orElse(null);
    }

    private void loadBoundActions(@NotNull CompoundTag nbt) {
        CompoundTag bounds = nbt.getCompound("bound_actions");
        for (String s : bounds.getAllKeys()) {
            int id = Integer.parseInt(s);
            ModRegistries.ACTIONS.getHolder(ResourceLocation.parse(bounds.getString(s))).ifPresentOrElse(h -> this.boundActions.put(id, h), () -> LOGGER.warn("Cannot find bound action {}", bounds.getString(s)));
        }
    }

    @Override
    public void checkSkillTreeLocks() {
        if (this.player.level() instanceof ServerLevel level) {
            Registry<ISkillTree> registryAccess = this.player.level().registryAccess().registryOrThrow(VampirismRegistries.Keys.SKILL_TREE);
            getCurrentFactionPlayer().ifPresent(factionPlayer -> {
                factionPlayer.getSkillHandler().updateUnlockedSkillTrees(registryAccess.holders().filter(s -> s.value().unlockPredicate().matches(level, null, this.player)).collect(Collectors.toList()));
            });
        }
    }

    /**
     * Notify faction about changes.
     * {@link FactionPlayerHandler#currentFaction} and {@link FactionPlayerHandler#currentLevel} will be used as the new ones
     */
    private void notifyFaction(@Nullable Holder<? extends IPlayableFaction<?>> oldFaction, int oldLevel) {
        if (oldFaction != null && !oldFaction.equals(currentFaction)) {
            LOGGER.debug(LogUtil.FACTION, "{} is leaving faction {}", this.player.getName().getString(), oldFaction.getRegisteredName());
            VampirismLogger.info(VampirismLogger.LEVEL, "{} is leaving faction {}", this.player.getName().getString(), oldFaction.getRegisteredName());
            oldFaction.value().getPlayerCapability(player).ifPresent(c -> c.onLevelChanged(0, oldLevel));
        }
        if (currentFaction != null) {
            LOGGER.debug(LogUtil.FACTION, "{} has new faction level {} {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel);
            currentFaction.value().getPlayerCapability(player).ifPresent(c -> c.onLevelChanged(currentLevel, Objects.equals(oldFaction, currentFaction) ? oldLevel : 0));
        }
        ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.FACTION_CRITERIA, currentFaction == null ? 0 : currentFaction.value().hashCode());
    }

    private boolean setLordLevel(int level, boolean sync) {
        int oldLevel = this.currentLordLevel;
        if (level > 0 && (currentFaction == null || currentLevel != currentFaction.value().getHighestReachableLevel() || level > currentFaction.value().getHighestLordLevel())) {
            return false;
        }
        if (level < this.currentLordLevel) {
            //down leveling -> Reset tasks
            resetLordTasks(level);
        }

        this.currentLordLevel = level;
        this.checkSkillTreeLocks();
        this.updateCache();
        MinionWorldData.getData(player.level()).ifPresent(data -> {
            PlayerMinionController c = data.getController(this.player.getUUID());
            if (c != null) {
                c.setMaxMinions(this.currentFaction, this.getMaxMinions());
            }
        });
        if (level == 0) {
            LOGGER.debug(LogUtil.FACTION, "Resetting lord level for {}", this.player.getName().getString());
            VampirismLogger.info(VampirismLogger.LORD_LEVEL, "Resetting lord level for {}", this.player.getName().getString());
        } else {
            LOGGER.debug(LogUtil.FACTION, "{} has now lord level {}", this.player.getName().getString(), level);
            VampirismLogger.info(VampirismLogger.LORD_LEVEL, "{} has now lord level {}", this.player.getName().getString(), level);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (currentLordLevel < oldLevel){
                ModAdvancements.TRIGGER_FACTION.get().revokeLevel(serverPlayer, currentFaction, FactionCriterionTrigger.Type.LORD, currentLordLevel);
            }
            ModAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
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
        atts.hunterLevel = getCurrentLevel(ModFactions.HUNTER);
        atts.vampireLevel = getCurrentLevel(ModFactions.VAMPIRE);
        atts.lordLevel = this.currentLordLevel;
        atts.faction = this.currentFaction;
    }

    private void writeBoundActions(@NotNull CompoundTag nbt) {
        CompoundTag bounds = new CompoundTag();
        for (Int2ObjectMap.Entry<Holder<IAction<?>>> entry : this.boundActions.int2ObjectEntrySet()) {
            entry.getValue().unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).ifPresent(id -> {
                bounds.putString(String.valueOf(entry.getIntKey()), id);
            });
        }
        nbt.put("bound_actions", bounds);
    }



    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        Optional.ofNullable(this.currentFaction).flatMap(Holder::unwrapKey).map(ResourceKey::location).map(ResourceLocation::toString).ifPresent(faction -> {
            nbt.putString("faction", faction);
            nbt.putInt("level", currentLevel);
            nbt.putInt("lord_level", currentLordLevel);
        });
        nbt.putString("title_gender", titleGender.name());

        writeBoundActions(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("faction")) {
            currentFaction = getFactionFromKey(ResourceLocation.parse(nbt.getString("faction")));
            if (currentFaction == null) {
                LOGGER.warn("Could not find faction {}. Did mods change?", nbt.getString("faction"));
            } else {
                currentLevel = Math.min(nbt.getInt("level"), this.currentFaction.value().getHighestReachableLevel());
                currentLordLevel = Math.min(nbt.getInt("lord_level"), this.currentFaction.value().getHighestLordLevel());
                notifyFaction(null, 0);
            }
        }
        if (nbt.contains("title_gender")) {
            this.titleGender = IPlayableFaction.TitleGender.valueOf(nbt.getString("title_gender"));
        }
        loadBoundActions(nbt);
        updateCache();
    }

    @Override
    public String nbtKey() {
        return NBT_KEY;
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, FactionPlayerHandler> {

        @Override
        public @NotNull FactionPlayerHandler read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            if (holder instanceof Player player) {
                FactionPlayerHandler handler = new FactionPlayerHandler(player);
                handler.deserializeNBT(provider, tag);
                return handler;
            }
            throw new IllegalStateException("Cannot deserialize FactionPlayerHandler for non player entity");
        }

        @Override
        public CompoundTag write(FactionPlayerHandler attachment, HolderLookup.@NotNull Provider provider) {
            return attachment.serializeNBT(provider);
        }
    }

    public static class Factory implements Function<IAttachmentHolder, FactionPlayerHandler> {

        @Override
        public FactionPlayerHandler apply(IAttachmentHolder holder) {
            if (holder instanceof Player player) {
                return new FactionPlayerHandler(player);
            }
            throw new IllegalArgumentException("Cannot create faction player handler attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }
}
