package de.teamlapen.vampirism.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.items.ItemObsidianArmor;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends VampirismPlayer<IHunterPlayer> implements IHunterPlayer {//TODO Capabilities

    @CapabilityInject(IHunterPlayer.class)
    public final static Capability<IHunterPlayer> CAP = getNull();

    /**
     * Don't call before the construction event of the player entity is finished
     */
    public static HunterPlayer get(@Nonnull EntityPlayer player) {
        return (HunterPlayer) player.getCapability(CAP, null);
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IHunterPlayer.class, new Storage(), HunterPlayerDefaultImpl.class);
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityProvider createNewCapability(final EntityPlayer player) {
        return new ICapabilitySerializable<NBTTagCompound>() {

            IHunterPlayer inst = new HunterPlayer(player);

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
                return capability == CAP ? CAP.<T>cast(inst) : null;
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final ActionHandler<IHunterPlayer> actionHandler;
    private final SkillHandler<IHunterPlayer> skillHandler;
    private final HunterPlayerSpecialAttribute specialAttributes;

    public HunterPlayer(EntityPlayer player) {
        super(player);
        actionHandler = new ActionHandler<>(this);
        skillHandler = new SkillHandler<>(this);
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
        return player.isPotionActive(ModPotions.disguise_as_vampire) ? VReference.VAMPIRE_FACTION : getFaction();
    }

    @Override
    public int getMaxLevel() {
        return REFERENCE.HIGHEST_HUNTER_LEVEL;
    }

    @Override
    public Predicate<Entity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        if (otherFactionPlayers) {
            return entity -> true;
        } else {
            return VampirismAPI.factionRegistry().getPredicate(getFaction(), ignoreDisguise);
        }
    }

    @Override
    public ISkillHandler<IHunterPlayer> getSkillHandler() {
        return skillHandler;
    }

    public HunterPlayerSpecialAttribute getSpecialAttributes() {
        return this.specialAttributes;
    }

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public boolean isDisguised() {
        return player.isPotionActive(ModPotions.disguise_as_vampire);
    }

    public void loadData(NBTTagCompound compound) {
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
            if (ItemObsidianArmor.isFullyEquipped(player)) {
                return true;
            }
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

            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.ATTACK_DAMAGE, "Hunter", getLevel(), Balance.hp.STRENGTH_LCAP, Balance.hp.STRENGTH_MAX_MOD, Balance.hp.STRENGTH_TYPE, 2, false);
            if (level > 0) {
                if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
                    //When loading from NBT the playerNetServerHandler is not always initialized, but that's required for achievements. So checking here
                    //TODO player.addStat(Achievements.becomingAHunter, 1);
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
        player.getEntityWorld().profiler.startSection("vampirism_hunterPlayer");
        int level = getLevel();
        if (!isRemote()) {
            if (level > 0) {
                boolean sync = false;
                boolean syncToAll = false;
                NBTTagCompound syncPacket = new NBTTagCompound();
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
        }
        player.getEntityWorld().profiler.endSection();
    }

    @Override
    public void onUpdatePlayer(TickEvent.Phase phase) {

    }

    public void saveData(NBTTagCompound compound) {
        actionHandler.saveToNbt(compound);
        skillHandler.saveToNbt(compound);
    }

    @Override
    protected VampirismPlayer copyFromPlayer(EntityPlayer old) {
        HunterPlayer oldHunter = get(old);
        NBTTagCompound nbt = new NBTTagCompound();
        oldHunter.saveData(nbt);
        this.loadData(nbt);
        return oldHunter;
    }

    @Override
    protected void loadUpdate(NBTTagCompound nbt) {
        actionHandler.readUpdateFromServer(nbt);
        skillHandler.readUpdateFromServer(nbt);
    }

    @Override
    protected void writeFullUpdate(NBTTagCompound nbt) {
        actionHandler.writeUpdateForClient(nbt);
        skillHandler.writeUpdateForClient(nbt);
    }

    private static class Storage implements Capability.IStorage<IHunterPlayer> {
        @Override
        public void readNBT(Capability<IHunterPlayer> capability, IHunterPlayer instance, EnumFacing side, INBTBase nbt) {
            ((HunterPlayer) instance).loadData((NBTTagCompound) nbt);
        }

        @Override
        public INBTBase writeNBT(Capability<IHunterPlayer> capability, IHunterPlayer instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            ((HunterPlayer) instance).saveData(nbt);
            return nbt;
        }
    }
}
