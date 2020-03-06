package de.teamlapen.vampirism.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.client.gui.SleepInMultiplayerModScreen;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.items.ObsidianArmorItem;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;
import static de.teamlapen.vampirism.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.blocks.TentBlock.POSITION;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends VampirismPlayer<IHunterPlayer> implements IHunterPlayer {

    private static final Logger LOGGER = LogManager.getLogger(HunterPlayer.class);

    @CapabilityInject(IHunterPlayer.class)
    public static Capability<IHunterPlayer> CAP = getNull();

    /**
     * Don't call before the construction event of the player entity is finished
     * Must check Entity#isAlive before
     */
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
    private final HunterPlayerSpecialAttribute specialAttributes;

    public HunterPlayer(PlayerEntity player) {
        super(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this, VReference.HUNTER_FACTION);
        specialAttributes = new HunterPlayerSpecialAttribute();
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
        return player.isPotionActive(ModEffects.disguise_as_vampire) ? VReference.VAMPIRE_FACTION : getFaction();
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

    @Nonnull
    public HunterPlayerSpecialAttribute getSpecialAttributes() {
        return this.specialAttributes;
    }

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public boolean isDisguised() {
        return player.isPotionActive(ModEffects.disguise_as_vampire);
    }

    public void loadData(CompoundNBT compound) {
        actionHandler.loadFromNbt(compound);
        skillHandler.loadFromNbt(compound);
    }

    @Override
    public void onChangedDimension(DimensionType from, DimensionType to) {

    }

    @Override
    public void onDeath(DamageSource src) {
        actionHandler.deactivateAllActions();
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

            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.ATTACK_DAMAGE, "Hunter", getLevel(), getMaxLevel(), VampirismConfig.BALANCE.hpStrengthMaxMod.get(), VampirismConfig.BALANCE.hpStrengthType.get(), AttributeModifier.Operation.MULTIPLY_TOTAL, false);
            if (level > 0) {
                if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).connection != null) {
                    //When loading from NBT the playerNetServerHandler is not always initialized, but that's required for achievements. So checking here
                    player.addStat(ModStats.become_a_hunter);
                }

                if (oldLevel == 0) {
                    skillHandler.enableRootSkill();

                }
            } else {
                skillHandler.disableAllSkills();
                actionHandler.resetTimers();
            }

        } else {
            if (level == 0) {
                actionHandler.resetTimers();
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
        player.getEntityWorld().getProfiler().startSection("vampirism_hunterPlayer");
        int level = getLevel();
        if (!isRemote()) {
            if (level > 0) {
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
            }
        } else {
            if (level > 0) {
                actionHandler.updateActions();
            }
            if (player.isSleeping()) {
                player.getBedPosition().ifPresent(pos -> {
                    if (player.world.getBlockState(pos).getBlock() instanceof TentBlock) {
                        if (Minecraft.getInstance().currentScreen instanceof SleepInMultiplayerScreen && !(Minecraft.getInstance().currentScreen instanceof SleepInMultiplayerModScreen))
                            Minecraft.getInstance().displayGuiScreen(new SleepInMultiplayerModScreen("text.vampirism.tent.stop_sleeping"));
                        TentBlock.setTentSleepPosition(player, pos, player.world.getBlockState(pos).get(POSITION), player.world.getBlockState(pos).get(FACING));
                    }
                });
            }
        }
        player.getEntityWorld().getProfiler().endSection();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {

    }

    public void saveData(CompoundNBT compound) {
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
        actionHandler.readUpdateFromServer(nbt);
        skillHandler.readUpdateFromServer(nbt);
    }

    @Override
    protected void writeFullUpdate(CompoundNBT nbt) {
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
