package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IVampirePlayer;
import de.teamlapen.vampirism.config.BalanceVampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer extends VampirismPlayer implements IVampirePlayer{

    private final BloodStats bloodStats;
    private final String KEY_EYE = "eye_type";
    private boolean sundamage_cache=false;
    private int eyeType = 0;
    public VampirePlayer(EntityPlayer player) {
        super(player);
        bloodStats = new BloodStats(player);
    }

    /**
     * Don't call before the construction event of the player entity is finished
     *
     * @param player
     * @return
     */
    public static VampirePlayer get(EntityPlayer player) {
        return (VampirePlayer) VampirismAPI.VAMPIRE_FACTION.getProp(player);
    }

    public static void register(EntityPlayer player) {
        player.registerExtendedProperties(VampirismAPI.VAMPIRE_FACTION.prop, new VampirePlayer(player));
    }

    /**
     * @return Eyetype for rendering
     */
    public int getEyeType() {
        return eyeType;
    }

    /**
     * Sets the eyeType as long as it is valid.
     * Also sends a sync packet if on server
     *
     * @param eyeType
     * @return Whether the type is valid or not
     */
    public boolean setEyeType(int eyeType) {
        if (eyeType >= REFERENCE.EYE_TYPE_COUNT || eyeType < 0) {
            return false;
        }
        if (eyeType != this.eyeType) {
            this.eyeType = eyeType;
            if (!player.worldObj.isRemote) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger(KEY_EYE, eyeType);
                sync(nbt, true);
            }
        }
        return true;
    }

    @Override
    protected int getMaxLevel() {
        return REFERENCE.HIGHEST_VAMPIRE_LEVEL;
    }

    @Override
    protected VampirismPlayer copyFromPlayer(EntityPlayer old) {
        return this;
    }

    @Override
    protected void onLevelChanged() {
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.movementSpeed, "Vampire", getLevel(), BalanceVampirePlayer.SPEED_LCAP, BalanceVampirePlayer.SPEED_MAX_MOD, BalanceVampirePlayer.SPEED_TYPE);
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.attackDamage, "Vampire", getLevel(), BalanceVampirePlayer.STRENGTH_LCAP, BalanceVampirePlayer.STRENGTH_MAX_MOD, BalanceVampirePlayer.STRENGTH_TYPE);
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.maxHealth, "Vampire", getLevel(), BalanceVampirePlayer.HEALTH_LCAP, BalanceVampirePlayer.HEALTH_MAX_MOD, BalanceVampirePlayer.HEALTH_TYPE);
        bloodStats.addExhaustionModifier("level", 1.0F + getLevel() / (float) getMaxLevel());
    }

    @Override
    public int getBloodLevel() {
        return bloodStats.getBloodLevel();
    }

    public BloodStats getBloodStats() {
        return bloodStats;
    }

    /**
     * Increases exhaustion level by supplied amount
     * TODO core mod hook into EntityPlayer
     */
    public void addExhaustion(float p_71020_1_) {
        if (!player.capabilities.disableDamage) {
            if (!player.worldObj.isRemote) {
                bloodStats.addExhaustion(p_71020_1_);
            }
        }
    }

    @Override
    public boolean isAutoFillEnabled() {
        return false;
    }

    @Override
    public boolean isVampireLord() {
        return false;
    }



    @Override
    public boolean canTurnOthers() {
        return getLevel() >= BalanceVampirePlayer.MIN_TURN_LEVEL;
    }

    @Override
    public void addExhaustionModifier(String id, float mod) {
        bloodStats.addExhaustionModifier(id, mod);
    }

    @Override
    public void removeExhaustionModifier(String id) {
        bloodStats.removeExhaustionModifier(id);
    }

    @Override
    public boolean isDisguised() {
        return false;//TODO implement
    }

    @Override
    public boolean isGettingSundamage(boolean forcerefresh) {
        if(player.ticksExisted%8==0){
            sundamage_cache= Helper.gettingSundamge(player);
        }
        return sundamage_cache;
    }

    @Override
    public boolean isGettingSundamge() {
        return isGettingSundamage(false);
    }


    @Override
    public void onJoinWorld() {

    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        return false;
    }

    @Override
    public void onDeath(DamageSource src) {

    }

    @Override
    public void onUpdate() {

        if (this.bloodStats.onUpdate()) {
            sync(this.bloodStats.writeUpdate(new NBTTagCompound()), false);
        }
    }

    @Override
    public void onChangedDimension(int from, int to) {

    }

    @Override
    public void onPlayerLoggedIn() {

    }

    @Override
    public void onPlayerLoggedOut() {

    }

    @Override
    public void onPlayerClone(EntityPlayer original) {
        copyFrom(original);
    }

    @Override
    public void saveData(NBTTagCompound nbt) {
        bloodStats.writeNBT(nbt);
        nbt.setInteger(KEY_EYE, eyeType);
    }

    @Override
    public void loadData(NBTTagCompound nbt) {
        bloodStats.readNBT(nbt);
        eyeType = nbt.getInteger(KEY_EYE);
    }

    @Override
    protected void loadUpdate(NBTTagCompound nbt) {
        if (nbt.hasKey(KEY_EYE)) {
            setEyeType(nbt.getInteger(KEY_EYE));
        }
        bloodStats.loadUpdate(nbt);
    }

    @Override
    public PlayableFaction<IVampirePlayer> getFaction() {
        return VampirismAPI.VAMPIRE_FACTION;
    }

    @Override
    public String getPropertyKey() {
        return VampirismAPI.VAMPIRE_FACTION.prop;
    }


    @Override
    protected void writeFullUpdate(NBTTagCompound nbt) {
        nbt.setInteger(KEY_EYE, getEyeType());
        bloodStats.writeUpdate(nbt);
    }
}
