package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer extends VampirismPlayer implements IVampirePlayer{


    private boolean sundamage_cache=false;
    public VampirePlayer(EntityPlayer player) {
        super(player);
    }

    @Override
    protected int getMaxLevel() {
        return REFERENCE.HIGHEST_VAMPIRE_LEVEL;
    }

    @Override
    protected VampirismPlayer copyFromPlayer(EntityPlayer old) {
        return this;
    }

    /**
     * Don't call before the construction event of the player entity is finished
     * @param player
     * @return
     */
    public static VampirePlayer get(EntityPlayer player){
        return (VampirePlayer)VampirismAPI.getVampirePlayer(player);
    }
    public static void register(EntityPlayer player){
        player.registerExtendedProperties(VampirismAPI.VP_EXT_PROP_NAME,new VampirePlayer(player));
    }


    @Override
    protected void onLevelChanged() {
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.movementSpeed,"Vampire",getLevel(), Balance.vp.SPEED_LCAP,Balance.vp.SPEED_MAX_MOD,Balance.vp.SPEED_TYPE);
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.attackDamage, "Vampire", getLevel(), Balance.vp.STRENGTH_LCAP, Balance.vp.STRENGTH_MAX_MOD, Balance.vp.STRENGTH_TYPE);
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.maxHealth,"Vampire",getLevel(), Balance.vp.HEALTH_LCAP,Balance.vp.HEALTH_MAX_MOD,Balance.vp.HEALTH_TYPE);

        super.onLevelChanged();
    }

    @Override
    public int getBloodLevel() {
        return 0;
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
        return getLevel()>=Balance.vp.MIN_TURN_LEVEL;
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
}
