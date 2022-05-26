package de.teamlapen.vampirism.player.hunter;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.items.ObsidianArmorItem;
import de.teamlapen.vampirism.player.FactionBasePlayer;
import de.teamlapen.vampirism.player.IVampirismPlayer;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
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

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends FactionBasePlayer<IHunterPlayer> implements IHunterPlayer {

    private static final Logger LOGGER = LogManager.getLogger(HunterPlayer.class);

    public static final Capability<IHunterPlayer> CAP = CapabilityManager.get(new CapabilityToken<>(){});

    /**
     * Don't call before the construction event of the player entity is finished
     * Must check Entity#isAlive before
     *
     * Always prefer calling #getOpt instead
     */
    @Deprecated
    public static HunterPlayer get(@Nonnull Player player) {
        return (HunterPlayer) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get HunterPlayer from player " + player));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<HunterPlayer> getOpt(@Nonnull Player player) {
        LazyOptional<HunterPlayer> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Hunter player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    public static ICapabilityProvider createNewCapability(final Player player) {
        return new ICapabilitySerializable<CompoundTag>() {

            final HunterPlayer inst = new HunterPlayer(player);
            final LazyOptional<IHunterPlayer> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                inst.loadData(nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                inst.saveData(tag);
                return tag;
            }
        };
    }

    private final ActionHandler<IHunterPlayer> actionHandler;
    private final SkillHandler<IHunterPlayer> skillHandler;

    public HunterPlayer(Player player) {
        super(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this, VReference.HUNTER_FACTION);
    }

    @Override
    public void breakDisguise() {
        if (actionHandler.isActionActive(HunterActions.disguise_hunter.get())) {
            actionHandler.toggleAction(HunterActions.disguise_hunter.get());
        }
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
    public ResourceLocation getCapKey() {
        return REFERENCE.HUNTER_PLAYER_KEY;
    }

    @Override
    public IFaction<?> getDisguisedAs() {
        return player.hasEffect(ModEffects.disguise_as_vampire.get()) ? VReference.VAMPIRE_FACTION : getFaction();
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

    @Nonnull
    @Override
    public ISkillHandler<IHunterPlayer> getSkillHandler() {
        return skillHandler;
    }

    /**
     * You can use {@link VampirismPlayerAttributes#getHuntSpecial()} instead if you don't have the hunter player already
     */
    @Nonnull
    public HunterPlayerSpecialAttribute getSpecialAttributes() {
        return ((IVampirismPlayer) player).getVampAtts().getHuntSpecial();
    }

    @Override
    public int getTheEntityID() {
        return player.getId();
    }

    @Override
    public boolean isDisguised() {
        return player.hasEffect(ModEffects.disguise_as_vampire.get());
    }

    public void loadData(CompoundTag compound) {
        super.loadData(compound);
        actionHandler.loadFromNbt(compound);
        skillHandler.loadFromNbt(compound);
    }

    @Override
    public void onChangedDimension(ResourceKey<Level> from, ResourceKey<Level> to) {

    }

    @Override
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        actionHandler.deactivateAllActions();
        if (src.getEntity() instanceof ServerPlayer && Helper.isVampire(((Player) src.getEntity())) && this.getRepresentingPlayer().getEffect(ModEffects.freeze.get()) != null) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger(((ServerPlayer) src.getEntity()), VampireActionTrigger.Action.KILL_FROZEN_HUNTER);
        }
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (DamageSource.ON_FIRE.equals(src) || DamageSource.IN_FIRE.equals(src)) {
            return ObsidianArmorItem.isFullyEquipped(player);
        }
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
                if (oldLevel == 0) {
                    skillHandler.enableRootSkill();

                }
            } else {
                skillHandler.disableAllSkills();
                actionHandler.resetTimers();
                this.skillHandler.resetRefinements();
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
                actionHandler.updateActions();
                VampirismMod.proxy.handleSleepClient(player);

            }
        }
        getSpecialAttributes().fullHunterCoat = level > 0 ? HunterCoatItem.isFullyEquipped(player) : null;

        player.getCommandSenderWorld().getProfiler().pop();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {

    }

    public void saveData(CompoundTag compound) {
        super.saveData(compound);
        actionHandler.saveToNbt(compound);
        skillHandler.saveToNbt(compound);
    }

    @Override
    protected FactionBasePlayer<IHunterPlayer> copyFromPlayer(Player old) {
        HunterPlayer oldHunter = get(old);
        CompoundTag nbt = new CompoundTag();
        oldHunter.saveData(nbt);
        this.loadData(nbt);
        return oldHunter;
    }

    @Override
    protected void loadUpdate(CompoundTag nbt) {
        super.loadUpdate(nbt);
        actionHandler.readUpdateFromServer(nbt);
        skillHandler.readUpdateFromServer(nbt);
    }

    @Override
    protected void writeFullUpdate(CompoundTag nbt) {
        super.writeFullUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
    }
}
