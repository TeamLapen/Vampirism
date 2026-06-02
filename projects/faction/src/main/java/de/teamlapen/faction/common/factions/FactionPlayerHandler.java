package de.teamlapen.faction.common.factions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.event.PlayerFactionEvent;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.refinements.IRefinementPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.api.factions.tasks.ITaskPlayer;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.*;
import de.teamlapen.faction.common.event.FactionEventFactory;
import de.teamlapen.faction.common.factions.actions.ActionKeys;
import de.teamlapen.faction.common.factions.minions.MinionWorldData;
import de.teamlapen.faction.common.factions.minions.PlayerMinionController;
import de.teamlapen.faction.common.tags.FactionTaskTags;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import de.teamlapen.faction.common.util.DamageHandler;
import de.teamlapen.faction.common.util.ModCodecs;
import de.teamlapen.faction.common.util.ScoreboardUtil;
import de.teamlapen.faction.common.world.ModDamageSources;
import de.teamlapen.faction.server.FactionLogger;
import de.teamlapen.sync.AttachmentSync;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler extends AttachmentSync implements IFactionPlayerHandler {
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

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ILordPlayer<T>> Optional<T> getLordPlayer() {
        return getCurrentFactionPlayer().filter(s -> s instanceof ILordPlayer<?>).map(s -> (T) s);
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
    public int getCurrentLevel(Holder<? extends IFaction<?>> f) {
        return isInFaction(f) ? currentLevel : 0;
    }

    @Override
    public float getCurrentLevelRelative() {
        return currentLevel / (float) currentFaction.value().getHighestReachableLevel();
    }

    @Override
    public int getLordLevel() {
        return currentLordLevel;
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
            setFaction(LevelingChange.builder().faction(faction).level(1).build());
        }
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (FactionConfig.server().factionPvpOnlyBetweenFactions.get() && src.getEntity() instanceof Player) {
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
        setFaction(LevelingChange.neutral());
        player.sendOverlayMessage(Component.translatable("command.factionapi.base.level.successful", player.getName(), oldFaction.value().getNameSingular(), 0));
        if (die) {
            DamageHandler.hurtModded((ServerLevel) this.player.level(), player, ModDamageSources::leaveFaction, 10000);
        }
    }

    @Override
    public void checkSkillTreeLocks() {
        if (this.player.level() instanceof ServerLevel level) {
            Registry<ISkillTree> registryAccess = this.player.level().registryAccess().lookupOrThrow(FactionRegistries.Keys.SKILL_TREE);
            getSkillHandler().ifPresent(handler -> handler.updateUnlockedSkillTrees(registryAccess.listElements().filter(s -> s.value().unlockPredicate().matches(level, null, this.player)).collect(Collectors.toList())));
        }
    }

    private void updateCache() {
        player.refreshDisplayName();
    }

    @Override
    public boolean setFaction(LevelingChange param) {
        var oldFaction = this.currentFaction;
        var oldLevel = this.currentLevel;
        var oldLordLevel = this.currentLordLevel;
        var newFaction = param.getNewFaction(oldFaction);
        boolean changedFaction = !IFaction.is(currentFaction, newFaction);
        int newLevel = oldLevel;
        int newLordLevel = oldLordLevel;

        if (changedFaction) {
            if (!param.hasLevelChange() && !param.hasLordLevelChange()) {
                newLevel = 1;
                newLordLevel = 0;
            } else if (!param.hasLordLevelChange()) {
                newLordLevel = 0;
            }
        }
         if (changedFaction && (!param.hasLevelChange() && !param.hasLordLevelChange())) {
            newLevel = 1;
            newLordLevel = 0;
        }

        if (param.hasLevelChange() && !param.hasLordLevelChange()) {
            newLevel = param.getNewLevel();
            if (newLevel < newFaction.value().getHighestReachableLevel()) {
                newLordLevel = 0;
            }
        }
        if (param.hasLordLevelChange()) {
            newLordLevel = param.getNewLordLevel();
            newLevel = newFaction.value().getHighestReachableLevel();
        }

        newLevel = Math.clamp(newLevel, 0, newFaction.value().getHighestReachableLevel());
        newLordLevel = Math.clamp(newLordLevel, 0, newFaction.value().getHighestLordLevel());

        if (changedFaction) {
            if (!this.currentFaction.value().getPlayerCapability(player).canLeaveFaction()) {
                LOGGER.info("You cannot leave faction {}, it is prevented by respective mod", currentFaction.getRegisteredName());
                return false;
            }
        }

        if (FactionEventFactory.fireChangeLevelOrFactionEvent(this, oldFaction, oldLevel, newFaction, newLevel)) {
            LOGGER.debug("Faction or Level change event canceled");
            return false;
        }

        if (changedFaction && factionPlayer() instanceof ITaskPlayer<?> taskPlayer) {
            taskPlayer.getTaskManager().reset();
        }

        if (changedFaction || newLordLevel < oldLordLevel) {
            resetLordTasks();
        }


        this.currentFaction = newFaction;
        this.currentLevel = newLevel;
        this.currentLordLevel = newLordLevel;

        param = param.copy()
                .level(this.currentLevel)
                .lordLevel(this.currentLordLevel)
                .faction(this.currentFaction).build();

        if (changedFaction) {
            oldFaction.value().getPlayerCapability(this.player).leaveFaction();
        }
        var newFactionData = newFaction.value().getPlayerCapability(this.player);
        newFactionData.levelChanged(param);

        this.checkSkillTreeLocks();
        this.updateCache();

        ScoreboardUtil.updateScoreboard(this.player, ScoreboardUtil.FACTION_CRITERIA, this.currentFaction.value().hashCode());

        if (newFactionData instanceof ILordPlayer<?> lordPlayer) {
            MinionWorldData.getData(this.player.level()).ifPresent(data -> {
                PlayerMinionController c = data.getController(this.player.getUUID());
                if (c != null) {
                    c.setMaxMinions(this.currentFaction, lordPlayer.getMaxMinions());
                }
            });
        }

        if (this.player instanceof ServerPlayer) {
            FactionLogger.info(FactionLogger.FACTION, param.toJson());
        }

        if (changedFaction || oldLevel != newLevel) {
            FactionEventFactory.fireFactionLevelChangedEvent(this, oldFaction, oldLevel, currentFaction, currentLevel);
        }

        FactionEventFactory.fireLevelChangedEvent(this, param);

        sync();
        if (player instanceof ServerPlayer serverPlayer) {
            FactionAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
        }
        return true;
    }


    //<editor-fold desc="Serialization">


    @Override
    public AttachmentType<?> getType() {
        return FactionAttachments.FACTION_PLAYER_HANDLER.get();
    }

    @Override
    protected void registerProperties() {
        registerProperty(FIdentifier.mod("faction")).simple(ModCodecs.playableFaction()).defaultValue(DefaultFactions.NEUTRAL).provider(() -> this.currentFaction).commonLoader(holder -> this.currentFaction = holder, Comparator.comparing(IHolderExtension::getKey)).register();
        registerProperty(FIdentifier.mod("level")).simple(0, () -> this.currentLevel, l -> this.currentLevel = l);
        registerProperty(FIdentifier.mod("lord_level")).simple(0, () -> this.currentLordLevel, l -> this.currentLordLevel = l);
        registerProperty(FIdentifier.mod("title_gender")).simple(IPlayableFaction.TitleGender.CODEC).defaultValue(IPlayableFaction.TitleGender.UNKNOWN).provider(() -> this.titleGender).commonLoader(l -> this.titleGender = l, Enum::compareTo).register();
        registerProperty(FIdentifier.mod("bound_action")).list(ActionBinding.CODEC).provider(() -> this.boundActions.entrySet().stream().map(s -> new ActionBinding(s.getKey(), s.getValue())).toList()).commonLoader((l) -> {
            this.boundActions.clear();
            this.boundActions.putAll(l.stream().collect(Collectors.toMap(ActionBinding::key, ActionBinding::action)));
            return true;
        });
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
