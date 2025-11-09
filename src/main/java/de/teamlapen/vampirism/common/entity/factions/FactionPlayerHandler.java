package de.teamlapen.vampirism.common.entity.factions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.sync.common.storage.Attachment;
import de.teamlapen.sync.common.storage.AttachmentSync;
import de.teamlapen.sync.common.storage.UpdateParams;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.*;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.IRefinementPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.ITaskPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.common.entity.player.ActionKeys;
import de.teamlapen.vampirism.common.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.common.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundPlaySoundEventPacket;
import de.teamlapen.vampirism.common.serialization.ModCodecs;
import de.teamlapen.vampirism.common.tags.ModTaskTags;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.LogUtil;
import de.teamlapen.vampirism.common.util.ScoreboardUtil;
import de.teamlapen.vampirism.common.util.VampirismEventFactory;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.saved.MinionWorldData;
import de.teamlapen.vampirism.server.VampirismLogger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler extends Attachment implements IFactionPlayerHandler {
    private final static Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation SERIALIZER_ID = VResourceLocation.mod("faction_player_handler");

    public static FactionPlayerHandler get(Player player) {
        return player.getData(ModAttachments.FACTION_PLAYER_HANDLER.get());
    }

    /**
     * Resolves the FactionPlayerHandler capability (prints a warning message if not present) and returns an Optional of the current IFactionPlayer instance
     */
    public static <T extends IFactionPlayer<T>> @NotNull Optional<T> getCurrentFactionPlayer(Player player) {
        return get(player).getCurrentFactionPlayer();
    }

    private final Player player;
    @NotNull
    private final Map<ActionKeys, Holder<IAction<?>>> boundActions = new HashMap<>();
    private @NotNull Holder<? extends IPlayableFaction<?>> currentFaction = ModFactions.NEUTRAL;
    private int currentLevel = 0;
    private int currentLordLevel = 0;
    @NotNull
    private IPlayableFaction.TitleGender titleGender = IPlayableFaction.TitleGender.UNKNOWN;

    public FactionPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public AttachmentType<?> attachmentType() {
        return ModAttachments.FACTION_PLAYER_HANDLER.get();
    }

    @Override
    public Player asEntity() {
        return player;
    }

    @Override
    public boolean canJoin(Holder<? extends IPlayableFaction<?>> faction) {
        PlayerFactionEvent.CanJoinFaction.Behavior behavior = VampirismEventFactory.fireCanJoinFactionEvent(this, currentFaction, faction);
        if (behavior == PlayerFactionEvent.CanJoinFaction.Behavior.ONLY_WHEN_NO_FACTION) {
            return IFaction.is(currentFaction, ModFactions.NEUTRAL);
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
    public ResourceLocation getAttachedKey() {
        return SERIALIZER_ID;
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
    public @NotNull Optional<Holder<? extends IPlayableFaction<?>>> getLordFaction() {
        return currentLordLevel > 0 ? Optional.of(currentFaction) : Optional.of(ModFactions.NEUTRAL);
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
        return Optional.of(currentFaction).map(Holder::value).map(IPlayableFaction::lordTiles);
    }

    public int getMaxMinions() {
        return currentLordLevel * ModConfig.BALANCE.miMinionPerLordLevel.get();
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
            this.player.level().registryAccess().lookupOrThrow(VampirismRegistries.Keys.TASK).getTagOrEmpty(ModTaskTags.AWARDS_LORD_LEVEL).forEach(holder -> {
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
    public boolean setFactionAndLevel(@NotNull Holder<? extends IPlayableFaction<?>> faction, int level) {
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
        if (VampirismEventFactory.fireChangeLevelOrFactionEvent(this, old, oldLevel, faction, level)) {
            LOGGER.debug("Faction or Level change event canceled");
            return false;
        }
        if (IFaction.is(faction, this.currentFaction) && factionPlayer() instanceof ITaskPlayer<?> taskPlayer) {
            taskPlayer.getTaskManager().reset();
        }
        if (IFaction.is(faction, ModFactions.NEUTRAL)) {
            currentFaction = ModFactions.NEUTRAL;
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
            currentFaction = ModFactions.NEUTRAL;
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
                serverPlayer.connection.send(new ClientboundPlaySoundEventPacket(ModSounds.LEVEL_UP));
                VampirismLogger.info(VampirismLogger.LEVEL, "{} has new faction level {} {}, was {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel, oldLevel);
            } else if (!IFaction.is(currentFaction, ModFactions.NEUTRAL)) {
                serverPlayer.connection.send(new ClientboundPlaySoundEventPacket(ModSounds.LEVEL_UP));
                VampirismLogger.info(VampirismLogger.LEVEL, "{} is now in faction {} {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel);
            } else {
                VampirismLogger.info(VampirismLogger.LEVEL, "{} has now no level", this.player.getName().getString());
            }
        }
        if (old != currentFaction || oldLevel != currentLevel) {
            VampirismEventFactory.fireFactionLevelChangedEvent(this, old, oldLevel, currentFaction, currentLevel);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ModAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
        }
        sync();
        return true;

    }

    @Override
    public boolean setFactionLevel(@NotNull Holder<? extends IPlayableFaction<?>> faction, int level) {
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
        setFactionAndLevel(ModFactions.NEUTRAL, 0);
        player.displayClientMessage(Component.translatable("command.vampirism.base.level.successful", player.getName(), oldFaction.value().getName(), 0), true);
        if (die) {
            DamageHandler.hurtModded((ServerLevel) this.player.level(), player, ModDamageSources::leaveFaction, 10000);
        }
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private @NotNull Holder<? extends IPlayableFaction<?>> getFactionFromKey(ResourceLocation key) {
        Holder<IFaction<?>> faction = ModRegistries.FACTIONS.get(key).orElse(null);
        if (faction != null && faction.value() instanceof IPlayableFaction<?>) {
            return (Holder<? extends IPlayableFaction<?>>) (Object) faction;
        }
        return ModFactions.NEUTRAL;
    }

    @Override
    public void checkSkillTreeLocks() {
        if (this.player.level() instanceof ServerLevel level) {
            Registry<ISkillTree> registryAccess = this.player.level().registryAccess().lookupOrThrow(VampirismRegistries.Keys.SKILL_TREE);
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
            VampirismLogger.info(VampirismLogger.LEVEL, "{} is leaving faction {}", this.player.getName().getString(), oldFaction.getRegisteredName());
            oldFaction.value().getPlayerCapability(player).onLevelChanged(0, oldLevel);
        }
        if (!IFaction.is(currentFaction, ModFactions.NEUTRAL)) {
            LOGGER.debug(LogUtil.FACTION, "{} has new faction level {} {}", this.player.getName().getString(), currentFaction.getRegisteredName(), currentLevel);
            currentFaction.value().getPlayerCapability(player).onLevelChanged(currentLevel, Objects.equals(oldFaction, currentFaction) ? oldLevel : 0);
        }
        ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.FACTION_CRITERIA, currentFaction.value().hashCode());
    }

    private boolean setLordLevel(int level, boolean sync) {
        int oldLevel = this.currentLordLevel;
        if (level > 0 && (IFaction.is(this.currentFaction, ModFactions.NEUTRAL) || currentLevel != currentFaction.value().getHighestReachableLevel() || level > currentFaction.value().getHighestLordLevel())) {
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
            VampirismLogger.info(VampirismLogger.LORD_LEVEL, "Resetting lord level for {}", this.player.getName().getString());
        } else {
            LOGGER.debug(LogUtil.FACTION, "{} has now lord level {}", this.player.getName().getString(), level);
            VampirismLogger.info(VampirismLogger.LORD_LEVEL, "{} has now lord level {}", this.player.getName().getString(), level);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ModAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
        }
        if (sync) sync();
        return true;
    }

    private void updateCache() {
        player.refreshDisplayName();
        VampirismPlayerAttributes atts = ((IVampirismPlayer) player).vampirism$getVampAtts();
        atts.hunterLevel = getCurrentLevel(ModFactions.HUNTER);
        atts.vampireLevel = getCurrentLevel(ModFactions.VAMPIRE);
        atts.lordLevel = this.currentLordLevel;
        atts.faction = this.currentFaction;
    }

    //<editor-fold desc="Serialization">

    @Override
    public void serialize(ValueOutput output) {
        output.store("faction", ModCodecs.playableFaction(), this.currentFaction);
        output.putInt("level", currentLevel);
        output.putInt("lord_level", currentLordLevel);
        output.store("title_gender", IPlayableFaction.TitleGender.CODEC, titleGender);

        writeBoundActions(output);
    }

    @Override
    public void deserialize(ValueInput input) {

        this.currentFaction = input.read("faction", ModCodecs.playableFaction()).orElse(ModFactions.NEUTRAL);
        this.currentLevel = IFaction.isNeutral(this.currentFaction) ? 0 : input.getIntOr("level", 0);
        this.currentLordLevel = IFaction.isNeutral(this.currentFaction) ? 0 : input.getIntOr("lord_level", 0);
//        notifyFaction(null, 0);
        this.titleGender = input.read("title_gender", IPlayableFaction.TitleGender.CODEC).orElse(IPlayableFaction.TitleGender.UNKNOWN);

        loadBoundActions(input);
        updateCache();
    }

    @Override
    public void serializeUpdateInternal(ValueOutput output, UpdateParams params) {
        output.store("faction", ModCodecs.playableFaction(), this.currentFaction);
        output.putInt("level", currentLevel);
        output.putInt("lord_level", currentLordLevel);
        output.store("title_gender", IPlayableFaction.TitleGender.CODEC, titleGender);
        this.writeBoundActions(output);
    }

    @Override
    public void deserializeUpdate(ValueInput input) {
        Holder<? extends IPlayableFaction<?>> old = currentFaction;
        int oldLevel = currentLevel;
        input.read("faction", ModCodecs.playableFaction()).ifPresent(x -> {
            this.currentFaction = x;
            this.currentLevel = input.getIntOr("level", 0);
            this.currentLordLevel = input.getIntOr("lord_level", 0);
            if (!IFaction.is(old, currentFaction) || oldLevel != currentLevel) {
                VampirismEventFactory.fireFactionLevelChangedEvent(this, old, oldLevel, currentFaction, currentLevel);
            }
            notifyFaction(old, oldLevel);
        });

        input.read("title_gender", IPlayableFaction.TitleGender.CODEC).ifPresent(x -> this.titleGender = x);
        this.loadBoundActions(input);
        updateCache();
    }

    private void writeBoundActions(ValueOutput output) {
        var actionList = output.list("action_bindings", ActionBinding.CODEC);
        this.boundActions.entrySet().stream().map(entry -> new ActionBinding(entry.getKey(), entry.getValue())).forEach(actionList::add);
    }

    private void loadBoundActions(ValueInput input) {
        for (ActionBinding actionBindings : input.listOrEmpty("action_bindings", ActionBinding.CODEC)) {
            this.boundActions.put(actionBindings.key(), actionBindings.action());
        }
    }

    private record ActionBinding(ActionKeys key, Holder<IAction<?>> action) {
        public static final Codec<ActionBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ActionKeys.CODEC.fieldOf("key").forGetter(ActionBinding::key),
                ModRegistries.ACTIONS.holderByNameCodec().fieldOf("action").forGetter(ActionBinding::action)
        ).apply(instance, ActionBinding::new));
    }

    //</editor-fold>

    public static class AttachmentOptions extends AttachmentSync.PlayerOptions<FactionPlayerHandler> {

        @Override
        protected FactionPlayerHandler create(Player player) {
            return new FactionPlayerHandler(player);
        }
    }
}
