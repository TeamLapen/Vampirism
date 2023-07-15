package de.teamlapen.vampirism.entity.player.hunter;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.entity.player.FactionBasePlayer;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends FactionBasePlayer<IHunterPlayer> implements IHunterPlayer {

    private static final Logger LOGGER = LogManager.getLogger(HunterPlayer.class);

    public static final Capability<IHunterPlayer> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    /**
     * Don't call before the construction event of the player entity is finished
     * Must check Entity#isAlive before
     * <br>
     * Always prefer calling #getOpt instead
     */
    @Deprecated
    public static @NotNull HunterPlayer get(@NotNull Player player) {
        return (HunterPlayer) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get HunterPlayer from player " + player));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static @NotNull LazyOptional<HunterPlayer> getOpt(@NotNull Player player) {
        LazyOptional<HunterPlayer> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Hunter player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    public static @NotNull ICapabilityProvider createNewCapability(final Player player) {
        return new ICapabilitySerializable<CompoundTag>() {

            final HunterPlayer inst = new HunterPlayer(player);
            final LazyOptional<IHunterPlayer> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(@NotNull CompoundTag nbt) {
                inst.loadData(nbt);
            }

            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public @NotNull CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                inst.saveData(tag);
                return tag;
            }
        };
    }

    private final @NotNull ActionHandler<IHunterPlayer> actionHandler;
    private final @NotNull SkillHandler<IHunterPlayer> skillHandler;

    public HunterPlayer(Player player) {
        super(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this, VReference.HUNTER_FACTION);
    }

    @Override
    public void breakDisguise() {
        actionHandler.deactivateAction(HunterActions.DISGUISE_HUNTER.get());
    }

    @Override
    public boolean canLeaveFaction() {
        return true;
    }

    @Override
    public IActionHandler<IHunterPlayer> getActionHandler() {
        return actionHandler;
    }

    @Override
    public @NotNull ResourceLocation getCapKey() {
        return REFERENCE.HUNTER_PLAYER_KEY;
    }

    @Override
    public IFaction<?> getDisguisedAs() {
        return player.hasEffect(ModEffects.DISGUISE_AS_VAMPIRE.get()) ? VReference.VAMPIRE_FACTION : getFaction();
    }

    @Override
    public int getLevel() {
        return ((IVampirismPlayer) player).getVampAtts().hunterLevel;
    }

    @Override
    public int getMaxLevel() {
        return REFERENCE.HIGHEST_HUNTER_LEVEL;
    }

    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        if (otherFactionPlayers) {
            return entity -> true;
        } else {
            return VampirismAPI.factionRegistry().getPredicate(getFaction(), ignoreDisguise);
        }
    }

    @NotNull
    @Override
    public ISkillHandler<IHunterPlayer> getSkillHandler() {
        return skillHandler;
    }

    /**
     * You can use {@link VampirismPlayerAttributes#getHuntSpecial()} instead if you don't have the hunter player already
     */
    @NotNull
    public HunterPlayerSpecialAttribute getSpecialAttributes() {
        return ((IVampirismPlayer) player).getVampAtts().getHuntSpecial();
    }

    @Override
    public int getTheEntityID() {
        return player.getId();
    }

    @Override
    public boolean isDisguised() {
        return player.hasEffect(ModEffects.DISGUISE_AS_VAMPIRE.get());
    }

    public void loadData(@NotNull CompoundTag compound) {
        super.loadData(compound);
        actionHandler.loadFromNbt(compound);
        skillHandler.loadFromNbt(compound);
    }

    @Override
    public void onChangedDimension(ResourceKey<Level> from, ResourceKey<Level> to) {

    }

    @Override
    public void onDeath(@NotNull DamageSource src) {
        super.onDeath(src);
        actionHandler.deactivateAllActions();
        if (src.getEntity() instanceof ServerPlayer && Helper.isVampire(((Player) src.getEntity())) && this.getRepresentingPlayer().getEffect(ModEffects.FREEZE.get()) != null) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger(((ServerPlayer) src.getEntity()), VampireActionCriterionTrigger.Action.KILL_FROZEN_HUNTER);
        }
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        return false;
    }

    @Override
    public void onJoinWorld() {
        if (getLevel() > 0) {
            actionHandler.onActionsReactivated();
        }
    }

    @Override
    public void onLevelChanged(int level, int oldLevel) {
        if (!isRemote()) {
            ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.HUNTER_LEVEL_CRITERIA, level);
            LevelAttributeModifier.applyModifier(player, Attributes.ATTACK_DAMAGE, "Hunter", level, getMaxLevel(), VampirismConfig.BALANCE.hpStrengthMaxMod.get(), VampirismConfig.BALANCE.hpStrengthType.get(), AttributeModifier.Operation.MULTIPLY_BASE, false);
            if (level > 0) {
            } else {
                skillHandler.disableAllSkills();
                actionHandler.resetTimers();
                this.skillHandler.resetRefinements();
                this.sync(true);
            }

        } else {
            if (level == 0) {
                actionHandler.resetTimers();
                this.skillHandler.resetRefinements();
            }
        }

    }

    @Override
    public void onPlayerLoggedIn() {

    }

    @Override
    public void onPlayerLoggedOut() {

    }

    @Override
    public void onUpdate() {
        player.getCommandSenderWorld().getProfiler().push("vampirism_hunterPlayer");
        super.onUpdate();
        int level = getLevel();
        if (level > 0) {
            if (!isRemote()) {
                boolean sync = false;
                boolean syncToAll = false;
                CompoundTag syncPacket = new CompoundTag();
                if (actionHandler.updateActions()) {
                    sync = true;
                    syncToAll = true;
                    actionHandler.writeUpdateForClient(syncPacket);
                }
                if (skillHandler.isDirty()) {
                    sync = true;
                    skillHandler.writeUpdateForClient(syncPacket);
                }
                if (sync) {
                    sync(syncPacket, syncToAll);
                }
            } else {
                if (getSpecialAttributes().blessingSoundReference != null && !player.isUsingItem()) {
                    //Make sure the blessing sound is stopped when player is not using {@link BlessableItem}. This is necessary because onReleaseUsing is not called for other client players.
                    getSpecialAttributes().blessingSoundReference.stopPlaying();
                    getSpecialAttributes().blessingSoundReference = null;
                }
                actionHandler.updateActions();
                VampirismMod.proxy.handleSleepClient(player);

            }
        } else {
            if (this.player.level.getGameTime() % 100 == 16) {
                if(OilUtils.getEquippedArmorOils(this.player).size() > 0) {
                    this.player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 120, 0, false, false));
                }
            }
        }
        getSpecialAttributes().fullHunterCoat = level > 0 ? HunterCoatItem.isFullyEquipped(player) : null;

        player.getCommandSenderWorld().getProfiler().pop();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {

    }

    public void saveData(@NotNull CompoundTag compound) {
        super.saveData(compound);
        actionHandler.saveToNbt(compound);
        skillHandler.saveToNbt(compound);
    }

    @Override
    protected @NotNull FactionBasePlayer<IHunterPlayer> copyFromPlayer(@NotNull Player old) {
        HunterPlayer oldHunter = get(old);
        CompoundTag nbt = new CompoundTag();
        oldHunter.saveData(nbt);
        this.loadData(nbt);
        return oldHunter;
    }

    @Override
    protected void loadUpdate(@NotNull CompoundTag nbt) {
        super.loadUpdate(nbt);
        actionHandler.readUpdateFromServer(nbt);
        skillHandler.readUpdateFromServer(nbt);
    }

    @Override
    protected void writeFullUpdate(@NotNull CompoundTag nbt) {
        super.writeFullUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
    }

    @Override
    public void updateMinionAttributes(boolean increasedStats) {
        MinionWorldData.getData(this.player.level).flatMap(a -> FactionPlayerHandler.getOpt(this.player).map(a::getOrCreateController)).ifPresent(controller -> controller.contactMinions((minion) -> {
            (minion.getMinionData()).ifPresent(b -> ((HunterMinionEntity.HunterMinionData)b).setIncreasedStats(increasedStats));
            HelperLib.sync(minion);
        }));
    }
}
