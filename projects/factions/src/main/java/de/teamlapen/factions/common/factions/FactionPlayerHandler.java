package de.teamlapen.factions.common.factions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.actions.IActionHandler;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.event.PlayerFactionEvent;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.lord.ILordTitleProvider;
import de.teamlapen.factions.api.refinements.IRefinementHandler;
import de.teamlapen.factions.api.refinements.IRefinementPlayer;
import de.teamlapen.factions.api.skills.ISkillHandler;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.factions.api.tasks.ITaskManager;
import de.teamlapen.factions.api.tasks.ITaskPlayer;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.actions.ActionKeys;
import de.teamlapen.factions.common.config.ModConfig;
import de.teamlapen.factions.common.core.*;
import de.teamlapen.factions.common.event.FactionEventFactory;
import de.teamlapen.factions.common.minions.MinionWorldData;
import de.teamlapen.factions.common.minions.PlayerMinionController;
import de.teamlapen.factions.common.network.packets.client.ClientboundPlaySoundEventPacket;
import de.teamlapen.factions.common.tags.FactionTaskTags;
import de.teamlapen.factions.common.util.*;
import de.teamlapen.factions.common.world.ModDamageSources;
import de.teamlapen.factions.server.FactionLogger;
import de.teamlapen.sync.Attachment;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler extends Attachment implements IFactionPlayerHandler {
    private final static Logger LOGGER = LogManager.getLogger();

    public static FactionPlayerHandler get(Player player) {
        return player.getData(FactionAttachments.FACTION_PLAYER_HANDLER.get());
    }

    public static <T extends IFactionPlayer<T>> Optional<T> getCurrentFactionPlayer(Player player) {
        return get(player).getCurrentFactionPlayer();
    }

    private final Player player;
    private final Map<ActionKeys, Holder<IAction<?>>> boundActions = new HashMap<>();
    private Holder<? extends IPlayableFaction<?>> currentFaction = DefaultFactions.NEUTRAL;
    private int currentLevel = 0;
    private int currentLordLevel = 0;
    private IPlayableFaction.TitleGender titleGender = IPlayableFaction.TitleGender.UNKNOWN;

    public FactionPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public Player asEntity() {
        return player;
    }

    @Override
    public boolean canJoin(Holder<? extends IPlayableFaction<?>> faction) {
        PlayerFactionEvent.CanJoinFaction.Behavior behavior = FactionEventFactory.fireCanJoinFactionEvent(this, currentFaction, faction);
        if (behavior == PlayerFactionEvent.CanJoinFaction.Behavior.ONLY_WHEN_NO_FACTION) {
            return IFaction.is(currentFaction, DefaultFactions.NEUTRAL);
        }
        return behavior == PlayerFactionEvent.CanJoinFaction.Behavior.ALLOW;
    }

    @Override
    public boolean canLeaveFaction() {
        return currentFaction.value().getPlayerCapability(player).canLeaveFaction();
    }

    /**
     * @return action if bound
     */
    @Nullable
    public Holder<IAction<?>> getBoundAction(ActionKeys key) {
        return this.boundActions.get(key);
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> getFaction() {
        return currentFaction;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends IFactionPlayer<T>> T factionPlayer() {
        return (T) currentFaction.value().getPlayerCapability(player);
    }

    @Override
    public <T extends IFactionPlayer<T>> Optional<T> factionPlayer(Holder<IFaction<T>> faction) {
        if (IFaction.is(currentFaction, faction)) {
            return Optional.of(factionPlayer());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends IFactionPlayer<T>> Optional<T> getCurrentFactionPlayer() {
        return Optional.of(factionPlayer());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ISkillPlayer<T>> Optional<T> getCurrentSkillPlayer() {
        return this.getCurrentFactionPlayer().filter(s -> s instanceof ISkillPlayer<?>).map(s -> (T) s);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IRefinementPlayer<T>> Optional<T> getCurrentRefinementPlayer() {
        return this.getCurrentFactionPlayer().filter(s -> s instanceof IRefinementPlayer<?>).map(s -> (T) s);
    }

    @Override
    public <T extends ISkillPlayer<T>> Optional<ISkillHandler<T>> getSkillHandler() {
        return this.<T>getCurrentSkillPlayer().map(ISkillPlayer::getSkillHandler);
    }

    @Override
    public <T extends ISkillPlayer<T>> Optional<IActionHandler<T>> getActionHandler() {
        return this.<T>getCurrentSkillPlayer().map(ISkillPlayer::getActionHandler);
    }

    @Override
    public <T extends IRefinementPlayer<T>> Optional<IRefinementHandler<T>> getRefinementHandler() {
        return this.<T>getCurrentRefinementPlayer().map(IRefinementPlayer::getRefinementHandler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ITaskPlayer<T>> Optional<T> getTaskPlayer() {
        return getCurrentFactionPlayer().filter(s -> s instanceof ITaskPlayer<?>).map(s -> (T) s);
    }

    @Override
    public Optional<ITaskManager> getTaskManager() {
        return getTaskPlayer().map(ITaskPlayer::getTaskManager);
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public int getCurrentLevel(Holder<? extends IPlayableFaction<?>> f) {
        return isInFaction(f) ? currentLevel : 0;
    }

    @Override
    public float getCurrentLevelRelative() {
        return currentLevel / (float) currentFaction.value().getHighestReachableLevel();
    }

    @Override
    public Optional<Holder<? extends IPlayableFaction<?>>> getLordFaction() {
        return currentLordLevel > 0 ? Optional.of(currentFaction) : Optional.of(DefaultFactions.NEUTRAL);
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

    public Optional<ILordTitleProvider> lordTitles() {
        return Optional.of(currentFaction).map(Holder::value).map(IPlayableFaction::lordTiles);
    }

    public int getMaxMinions() {
        return currentLordLevel * ModConfig.SERVER.miMinionPerLordLevel.get();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public IPlayableFaction.TitleGender titleGender() {
        return this.titleGender;
    }

    @Override
    public <T extends IFaction<?>> boolean isInFaction(@Nullable Holder<T> f) {
        return IFaction.is(currentFaction, f);
    }

    @Override
    public <T extends IFaction<?>> boolean isInFaction(@Nullable TagKey<T> f) {
        return IFaction.is(currentFaction, f);
    }

    @Override
    public void joinFaction(Holder<? extends IPlayableFaction<?>> faction) {
        if (canJoin(faction)) {
            setFactionAndLevel(faction, 1);
        }
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (ModConfig.SERVER.pvpOnlyBetweenFactions.get() && src.getEntity() instanceof Player) {
            Holder<? extends IPlayableFaction<?>> otherFaction = get((Player) src.getEntity()).getFaction();
            return !IFaction.is(this.currentFaction, otherFaction);
        }
        return true;
    }

    /**
     * Reset all lord task that should be available for players at the given lord level
     */
    public void resetLordTasks() {
        getTaskManager().ifPresent(manager -> {
            this.player.level().registryAccess().lookupOrThrow(FactionRegistries.Keys.TASK).getTagOrEmpty(FactionTaskTags.AWARDS_LORD_LEVEL).forEach(holder -> {
                holder.unwrapKey().ifPresent(manager::resetUniqueTask);
            });
        });
    }

    public void setBoundAction(ActionKeys key, @Nullable Holder<IAction<?>> boundAction, boolean sync) {
        if (boundAction == null) {
            this.boundActions.remove(key);
        } else {
            this.boundActions.put(key, boundAction);
        }
        if (sync) {
            sync();
        }
    }

    @Override
    public boolean setFactionAndLevel(Holder<? extends IPlayableFaction<?>> faction, int level) {
        Holder<? extends IPlayableFaction<?>> old = currentFaction;
        int oldLevel = currentLevel;
        int newLordLevel = this.currentLordLevel;

        if (!IFaction.is(currentFaction, faction) || level == 0) {
            if (!currentFaction.value().getPlayerCapability(player).canLeaveFaction()) {
                LOGGER.info("You cannot leave faction {}, it is prevented by respective mod", currentFaction.getRegisteredName());
                return false;
            }
        }
        if (level < 0 || level > faction.value().getHighestReachableLevel()) {
            LOGGER.warn("Level {} in faction {} cannot be reached", level, faction.getRegisteredName());
            return false;
        }
        if (FactionEventFactory.fireChangeLevelOrFactionEvent(this, old, oldLevel, faction, level)) {
            LOGGER.debug("Faction or Level change event canceled");
            return false;
        }
        if (IFaction.is(faction, this.currentFaction) && factionPlayer() instanceof ITaskPlayer<?> taskPlayer) {
            taskPlayer.getTaskManager().reset();
        }
        if (IFaction.is(faction, DefaultFactions.NEUTRAL)) {
            currentFaction = DefaultFactions.NEUTRAL;
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
            currentFaction = DefaultFactions.NEUTRAL;
            newLordLevel = 0;
        }
        if (currentLordLevel != newLordLevel) {
            this.setLordLevel(newLordLevel, false);
        }
        this.checkSkillTreeLocks();
        updateCache();
        notifyFaction(old, oldLevel);
        if (this.player instanceof ServerPlayer serverPlayer && !(currentFaction == old && oldLevel == currentLevel)) {
            if (old == currentFaction) {
                serverPlayer.connection.send(new ClientboundPlaySoundEventPacket(FactionSounds.LEVEL_UP));
                FactionLogger.info(FactionLogger.LEVEL, "{} has new faction level {} {}, was {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel, oldLevel);
            } else if (!IFaction.is(currentFaction, DefaultFactions.NEUTRAL)) {
                serverPlayer.connection.send(new ClientboundPlaySoundEventPacket(FactionSounds.LEVEL_UP));
                FactionLogger.info(FactionLogger.LEVEL, "{} is now in faction {} {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel);
            } else {
                FactionLogger.info(FactionLogger.LEVEL, "{} has now no level", this.player.getName().getString());
            }
        }
        if (old != currentFaction || oldLevel != currentLevel) {
            FactionEventFactory.fireFactionLevelChangedEvent(this, old, oldLevel, currentFaction, currentLevel);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            FactionAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
        }

        sync();
        return true;

    }

    @Override
    public boolean setFactionLevel(Holder<? extends IPlayableFaction<?>> faction, int level) {
        return IFaction.is(faction, this.currentFaction) && setFactionAndLevel(faction, level);
    }

    @Override
    public boolean setLordLevel(int level) {
        return this.setLordLevel(level, true);
    }

    public boolean setTitleGender(boolean female) {
        var gender = female ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE;
        return this.setTitleGender(gender);
    }

    public boolean setTitleGender(IPlayableFaction.TitleGender female) {
        this.titleGender = female;
        player.refreshDisplayName();
        sync();
        return true;
    }

    @Override
    public void leaveFaction(boolean die) {
        Holder<? extends IFaction<?>> oldFaction = currentFaction;
        setFactionAndLevel(DefaultFactions.NEUTRAL, 0);
        player.displayClientMessage(Component.translatable("command.vampirism.base.level.successful", player.getName(), oldFaction.value().getName(), 0), true);
        if (die) {
            DamageHandler.hurtModded((ServerLevel) this.player.level(), player, ModDamageSources::leaveFaction, 10000);
        }
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private Holder<? extends IPlayableFaction<?>> getFactionFromKey(ResourceLocation key) {
        Holder<IFaction<?>> faction = ModRegistries.FACTIONS.get(key).orElse(null);
        if (faction != null && faction.value() instanceof IPlayableFaction<?>) {
            return (Holder<? extends IPlayableFaction<?>>) (Object) faction;
        }
        return DefaultFactions.NEUTRAL;
    }

    @Override
    public void checkSkillTreeLocks() {
        if (this.player.level() instanceof ServerLevel level) {
            Registry<ISkillTree> registryAccess = this.player.level().registryAccess().lookupOrThrow(FactionRegistries.Keys.SKILL_TREE);
            getSkillHandler().ifPresent(handler -> handler.updateUnlockedSkillTrees(registryAccess.listElements().filter(s -> s.value().unlockPredicate().matches(level, null, this.player)).collect(Collectors.toList())));
        }
    }

    /**
     * Notify faction about changes.
     * {@link FactionPlayerHandler#currentFaction} and {@link FactionPlayerHandler#currentLevel} will be used as the new ones
     */
    private void notifyFaction(@Nullable Holder<? extends IPlayableFaction<?>> oldFaction, int oldLevel) {
        if (oldFaction != null && !oldFaction.equals(currentFaction)) {
            LOGGER.debug(LogUtil.FACTION, "{} is leaving faction {}", this.player.getName().getString(), oldFaction.getRegisteredName());
            FactionLogger.info(FactionLogger.LEVEL, "{} is leaving faction {}", this.player.getName().getString(), oldFaction.getRegisteredName());
//            oldFaction.value().getPlayerCapability(player).onLevelChanged(0, oldLevel); TODO
        }
        if (!IFaction.is(currentFaction, DefaultFactions.NEUTRAL)) {
            LOGGER.debug(LogUtil.FACTION, "{} has new faction level {} {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel);
//            currentFaction.value().getPlayerCapability(player).onLevelChanged(currentLevel, Objects.equals(oldFaction, currentFaction) ? oldLevel : 0); TODO
        }
        ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.FACTION_CRITERIA, currentFaction.value().hashCode());
    }

    private boolean setLordLevel(int level, boolean sync) {
        int oldLevel = this.currentLordLevel;
        if (level > 0 && (IFaction.is(this.currentFaction, DefaultFactions.NEUTRAL) || currentLevel != currentFaction.value().getHighestReachableLevel() || level > currentFaction.value().getHighestLordLevel())) {
            return false;
        }
        if (level < this.currentLordLevel) {
            //down leveling -> Reset tasks
            resetLordTasks();
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
            FactionLogger.info(FactionLogger.LORD_LEVEL, "Resetting lord level for {}", this.player.getName().getString());
        } else {
            LOGGER.debug(LogUtil.FACTION, "{} has now lord level {}", this.player.getName().getString(), level);
            FactionLogger.info(FactionLogger.LORD_LEVEL, "{} has now lord level {}", this.player.getName().getString(), level);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            FactionAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
        }
        if (sync) sync();
        return true;
    }

    private void updateCache() {
        player.refreshDisplayName();
    }

    //<editor-fold desc="Serialization">


    @Override
    public AttachmentType<?> getType() {
        return FactionAttachments.FACTION_PLAYER_HANDLER.get();
    }

    @Override
    protected void registerProperties() {
        registerProperty(FResourceLocation.mod("faction"), ModCodecs.playableFaction(), DefaultFactions.NEUTRAL, () -> this.currentFaction, holder -> this.currentFaction = holder, Comparator.comparing(IHolderExtension::getKey),true);
        registerProperty(FResourceLocation.mod("level"), 0, () -> this.currentLevel, l -> this.currentLevel = l, true);
        registerProperty(FResourceLocation.mod("lord_level"), 0, () -> this.currentLordLevel, l -> this.currentLordLevel = l, true);
        registerEnumProperty(FResourceLocation.mod("title_gender"), IPlayableFaction.TitleGender.CODEC, IPlayableFaction.TitleGender.UNKNOWN, () -> this.titleGender, l -> this.titleGender = l, true);
        registerListProperty(FResourceLocation.mod("bound_action"), ActionBinding.CODEC, ArrayList::new, () -> this.boundActions.entrySet().stream().map(s -> new ActionBinding(s.getKey(), s.getValue())).toList(), (l) -> {
            this.boundActions.clear();
            this.boundActions.putAll(l.stream().collect(Collectors.toMap(ActionBinding::key, ActionBinding::action)));
            return true;
        }, true);
    }

    @Override
    protected void onPropertyChanged() {
        this.player.refreshDisplayName();
    }

    private record ActionBinding(ActionKeys key, Holder<IAction<?>> action) {
        public static final Codec<ActionBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ActionKeys.CODEC.fieldOf("key").forGetter(ActionBinding::key),
                ModRegistries.ACTIONS.holderByNameCodec().fieldOf("action").forGetter(ActionBinding::action)
        ).apply(instance, ActionBinding::new));
    }

    //</editor-fold>

    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<FactionPlayerHandler> {

        @Override
        protected FactionPlayerHandler create(Player player) {
            return new FactionPlayerHandler(player);
        }
    }
}
