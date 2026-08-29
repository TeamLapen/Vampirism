package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.event.PlayerFactionEvent;
import de.teamlapen.faction.api.factions.*;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.factions.level.FactionUpdate;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.*;
import de.teamlapen.faction.common.event.FactionEventFactory;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import de.teamlapen.faction.common.util.DamageHandler;
import de.teamlapen.faction.common.util.ModCodecs;
import de.teamlapen.faction.common.util.ScoreboardUtil;
import de.teamlapen.faction.common.world.ModDamageSources;
import de.teamlapen.faction.common.world.entities.IPlayerEventListener;
import de.teamlapen.faction.common.world.inventory.FactionMenu;
import de.teamlapen.faction.server.FactionLogger;
import de.teamlapen.sync.AttachmentSync;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler extends AttachmentSync implements IFactionPlayerHandler, IPlayerEventListener {
    private final static Logger LOGGER = LogManager.getLogger();

    public static FactionPlayerHandler get(Player player) {
        return player.getData(FactionAttachments.FACTION_PLAYER_HANDLER.get());
    }

    public static <T extends IFactionPlayer<T>> Optional<T> getCurrentFactionPlayer(Player player) {
        return Optional.of(get(player).factionPlayer());
    }

    private final Player player;
    private Holder<? extends IPlayableFaction<?>> currentFaction = DefaultFactions.NEUTRAL;
    private int currentLevel = 0;

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

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ISkillPlayer<T>> Optional<T> getCurrentSkillPlayer() {
        if (factionPlayer() instanceof ISkillPlayer<?> skillPlayer) {
            return Optional.of((T)skillPlayer);
        }
        return Optional.empty();
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
    public <TInterface> Optional<TInterface> getExtension(Class<TInterface> type) {
        FactionExtensionType<TInterface> extension = currentFaction.value().extension(type);
        return extension == null ? Optional.empty() : Optional.of(extension.get(player));
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
            setFaction(FactionUpdate.builder().faction(faction).level(1).build());
        }
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (FactionConfig.server().factionPvpOnlyBetweenFactions.get() && src.getEntity() instanceof Player) {
            Holder<? extends IPlayableFaction<?>> otherFaction = get((Player) src.getEntity()).getFaction();
            return IFaction.is(this.currentFaction, otherFaction);
        }
        return false;
    }

    @Override
    public void leaveFaction(boolean die) {
        Holder<? extends IFaction<?>> oldFaction = currentFaction;
        setFaction(FactionUpdate.neutral());
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

    @Override
    public void onRespawn() {
        if (!IFaction.isNeutral(this.currentFaction)) {
            this.player.addEffect(new MobEffectInstance(FactionEffects.RESURRECTION_FATIGUE, 300));
        }
    }

    @Override
    public void onPlayerLoggedIn() {
        this.player.refreshDisplayName();
    }

    @Override
    public boolean setFaction(FactionUpdate param) {
        var oldFaction = this.currentFaction;
        var oldLevel = this.currentLevel;
        var newFaction = param.getFaction(oldFaction);
        boolean changedFaction = !IFaction.is(currentFaction, newFaction);
        int newLevel = oldLevel;

        if (changedFaction && !param.hasLevelChange()) {
            newLevel = 1;
        }
        if (param.hasLevelChange()) {
            newLevel = param.getLevel();
        }

        newLevel = Math.clamp(newLevel, 0, newFaction.value().getHighestReachableLevel());

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

        final var finalParam = param.copy()
                .level(newLevel)
                .faction(newFaction).build();
        param = finalParam;

        if (changedFaction) {
            oldFaction.value().getExtensions().values().forEach(extension -> extension.cleanup(this.player));
            oldFaction.value().getPlayerCapability(this.player).leaveFaction();
        }

        this.currentFaction = newFaction;
        this.currentLevel = newLevel;

        newFaction.value().getPlayerCapability(this.player).levelChanged(finalParam);
        newFaction.value().getExtensions().values().forEach(extension -> {
            if (extension.get(player) instanceof IFactionExtension fe) {
                fe.setLevel(finalParam);
            }
        });

        this.checkSkillTreeLocks();

        ScoreboardUtil.updateScoreboard(this.player, ScoreboardUtil.FACTION_CRITERIA, this.currentFaction.value().hashCode());

        if (this.player instanceof ServerPlayer) {
            FactionLogger.info(FactionLogger.FACTION, param.toJson());
        }

        if (changedFaction || oldLevel != newLevel) {
            FactionEventFactory.fireFactionLevelChangedEvent(this, oldFaction, oldLevel, currentFaction, currentLevel);
        }

        FactionEventFactory.fireLevelChangedEvent(this, param);

        this.player.refreshDisplayName();
        sync();
        if (this.player instanceof ServerPlayer serverPlayer) {
            FactionAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel);
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
        super.registerProperties();
        registerProperty(FIdentifier.mod("faction")).simple(ModCodecs.playableFaction()).defaultValue(DefaultFactions.NEUTRAL).provider(() -> this.currentFaction).commonLoader(holder -> this.currentFaction = holder, Comparator.comparing(IHolderExtension::getKey)).register();
        registerProperty(FIdentifier.mod("level")).simple(0, () -> this.currentLevel, l -> this.currentLevel = l);
    }

    @Override
    protected void onPropertyChanged() {
        this.player.refreshDisplayName();
    }

    //</editor-fold>

    public void openFactionMenu() {
        if (!player.isAlive()) return;
        if (IFaction.isNeutral(getFaction())) return;
        player.openMenu(new SimpleMenuProvider((i, inventory, player) -> new FactionMenu(i, inventory), Component.empty()));
        getTaskManager().ifPresent(ITaskManager::initializeFactionMenu);
    }

    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<FactionPlayerHandler> {

        @Override
        protected FactionPlayerHandler create(Player player) {
            return new FactionPlayerHandler(player);
        }
    }
}
