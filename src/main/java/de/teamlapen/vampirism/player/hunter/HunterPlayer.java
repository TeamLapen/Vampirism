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
import de.teamlapen.vampirism.player.IVampirismPlayer;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends VampirismPlayer<IHunterPlayer> implements IHunterPlayer {

    private static final Logger LOGGER = LogManager.getLogger(HunterPlayer.class);

    @CapabilityInject(IHunterPlayer.class)
    public static Capability<IHunterPlayer> CAP = getNull();

    /**
     * Always prefer #getOpt
     * Don't call before the construction event of the player entity is finished
     * Must check Entity#isAlive before
     */
    @Deprecated
    public static HunterPlayer get(@Nonnull PlayerEntity player) {
        return (HunterPlayer) player.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get HunterPlayer from player " + player));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<HunterPlayer> getOpt(@Nonnull PlayerEntity player) {
        LazyOptional<HunterPlayer> opt = player.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get Hunter player capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IHunterPlayer.class, new Storage(), HunterPlayerDefaultImpl::new);
    }

    public static ICapabilityProvider createNewCapability(final PlayerEntity player) {
        return new ICapabilitySerializable<CompoundNBT>() {

            final IHunterPlayer inst = new HunterPlayer(player);
            final LazyOptional<IHunterPlayer> opt = LazyOptional.of(() -> inst);

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

    private final ActionHandler<IHunterPlayer> actionHandler;
    private final SkillHandler<IHunterPlayer> skillHandler;

    public HunterPlayer(PlayerEntity player) {
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
    public ResourceLocation getCapKey() {
        return REFERENCE.HUNTER_PLAYER_KEY;
    }

    @Override
    public IFaction getDisguisedAs() {
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
        return player.hasEffect(ModEffects.DISGUISE_AS_VAMPIRE.get());
    }

    public void loadData(CompoundNBT compound) {
        super.loadData(compound);
        actionHandler.loadFromNbt(compound);
        skillHandler.loadFromNbt(compound);
    }

    @Override
    public void onChangedDimension(RegistryKey<World> from, RegistryKey<World> to) {

    }

    @Override
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        actionHandler.deactivateAllActions();
        if (src.getEntity() instanceof ServerPlayerEntity && Helper.isVampire(((PlayerEntity) src.getEntity())) && this.getRepresentingPlayer().getEffect(ModEffects.FREEZE.get()) != null) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger(((ServerPlayerEntity) src.getEntity()), VampireActionTrigger.Action.KILL_FROZEN_HUNTER);
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
                CompoundNBT syncPacket = new CompoundNBT();
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
        }
        getSpecialAttributes().fullHunterCoat = level > 0 ? HunterCoatItem.isFullyEquipped(player) : null;

        player.getCommandSenderWorld().getProfiler().pop();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {

    }

    public void saveData(CompoundNBT compound) {
        super.saveData(compound);
        actionHandler.saveToNbt(compound);
        skillHandler.saveToNbt(compound);
    }

    @Override
    protected VampirismPlayer copyFromPlayer(PlayerEntity old) {
        HunterPlayer oldHunter = get(old);
        CompoundNBT nbt = new CompoundNBT();
        oldHunter.saveData(nbt);
        this.loadData(nbt);
        return oldHunter;
    }

    @Override
    protected void loadUpdate(CompoundNBT nbt) {
        super.loadUpdate(nbt);
        actionHandler.readUpdateFromServer(nbt);
        skillHandler.readUpdateFromServer(nbt);
    }

    @Override
    protected void writeFullUpdate(CompoundNBT nbt) {
        super.writeFullUpdate(nbt);
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
    }

    private static class Storage implements Capability.IStorage<IHunterPlayer> {
        @Override
        public void readNBT(Capability<IHunterPlayer> capability, IHunterPlayer instance, Direction side, INBT nbt) {
            ((HunterPlayer) instance).loadData((CompoundNBT) nbt);
        }

        @Override
        public INBT writeNBT(Capability<IHunterPlayer> capability, IHunterPlayer instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            ((HunterPlayer) instance).saveData(nbt);
            return nbt;
        }
    }
}
