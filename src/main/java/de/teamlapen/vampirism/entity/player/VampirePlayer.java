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

    private final BloodStats bloodStats = new BloodStats();//TODO sync blood
    private boolean sundamage_cache=false;
    public VampirePlayer(EntityPlayer player) {
        super(player);
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
        super.onLevelChanged();
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
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        bloodStats.writeNBT(nbt);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        bloodStats.readNBT(nbt);
    }

    @Override
    public PlayableFaction<IVampirePlayer> getFaction() {
        return VampirismAPI.VAMPIRE_FACTION;
    }
}
