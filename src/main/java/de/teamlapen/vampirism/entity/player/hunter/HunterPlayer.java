package de.teamlapen.vampirism.entity.player.hunter;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.entity.player.VampirismPlayer;
import de.teamlapen.vampirism.entity.player.actions.ActionHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends VampirismPlayer<IHunterPlayer> implements IHunterPlayer {

    @CapabilityInject(IHunterPlayer.class)
    public final static Capability<IHunterPlayer> CAP = null;
    /**
     * Don't call before the construction event of the player entity is finished
     *
     * @param player
     * @return
     */
    public static HunterPlayer get(EntityPlayer player) {
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
            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                return capability == CAP ? (T) (inst) : null;//TODO switch to something like SLEEP_CAP.<T>cast(inst) in 1.9
            }

            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                return capability == CAP;
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private final ActionHandler<IHunterPlayer> actionHandler;
    private final SkillHandler<IHunterPlayer> skillHandler;

    public HunterPlayer(EntityPlayer player) {
        super(player);
        actionHandler = new ActionHandler<IHunterPlayer>(this);
        skillHandler = new SkillHandler<IHunterPlayer>(this);
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
        return player.isPotionActive(ModPotions.disguiseAsVampire) ? VReference.VAMPIRE_FACTION : getFaction();
    }

    @Override
    public IPlayableFaction<IHunterPlayer> getFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public Predicate<? super Entity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        if (otherFactionPlayers) {
            return Predicates.alwaysTrue();
        } else {
            return VampirismAPI.factionRegistry().getPredicate(getFaction(), ignoreDisguise);
        }
    }

    @Override
    public ISkillHandler<IHunterPlayer> getSkillHandler() {
        return skillHandler;
    }

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public boolean isDisguised() {
        return player.isPotionActive(ModPotions.disguiseAsVampire);
    }

    public void loadData(NBTTagCompound compound) {
        actionHandler.loadFromNbt(compound);
        skillHandler.loadFromNbt(compound);
    }

    @Override
    public void onChangedDimension(int from, int to) {

    }

    @Override
    public void onDeath(DamageSource src) {
        actionHandler.deactivateAllActions();
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
    public void onLevelChanged(int old, int level) {
        if (!isRemote()) {
            LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.ATTACK_DAMAGE, "Hunter", getLevel(), Balance.hp.STRENGTH_LCAP, Balance.hp.STRENGTH_MAX_MOD, Balance.hp.STRENGTH_TYPE);
            actionHandler.resetTimers();
            if (level > 0 && old == 0) {
                skillHandler.enableRootSkill();
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
                if (sync) {
                    sync(syncPacket, syncToAll);
                }
            }
        } else {
            if (level > 0) {
                actionHandler.updateActions();
            }
        }
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
        return get(old);//TODO
    }

    @Override
    protected int getMaxLevel() {
        return REFERENCE.HIGHEST_HUNTER_LEVEL;
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
        public void readNBT(Capability<IHunterPlayer> capability, IHunterPlayer instance, EnumFacing side, NBTBase nbt) {
            ((HunterPlayer) instance).loadData((NBTTagCompound) nbt);
        }

        @Override
        public NBTBase writeNBT(Capability<IHunterPlayer> capability, IHunterPlayer instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            ((HunterPlayer) instance).saveData(nbt);
            return nbt;
        }
    }
}
