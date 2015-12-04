package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IHunterPlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends VampirismPlayer implements IHunterPlayer {


    public HunterPlayer(EntityPlayer player) {
        super(player);
    }

    /**
     * Don't call before the construction event of the player entity is finished
     * @param player
     * @return
     */
    public static HunterPlayer get(EntityPlayer player){
        return (HunterPlayer) VampirismAPI.getHunterPlayer(player);
    }
    public static void register(EntityPlayer player){
        player.registerExtendedProperties(VampirismAPI.HP_EXT_PROP_NAME,new HunterPlayer(player));
    }

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {

    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {

    }

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public EntityPlayer getRepresentingPlayer() {
        return player;
    }

    @Override
    protected int getMaxLevel() {
        return REFERENCE.HIGHEST_HUNTER_LEVEL;
    }

    @Override
    public void sync(boolean all) {

    }


    @Override
    protected void onLevelChanged() {
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.attackDamage, "Hunter", getLevel(), Balance.hp.STRENGTH_LCAP, Balance.hp.STRENGTH_MAX_MOD, Balance.hp.STRENGTH_TYPE);
        super.onLevelChanged();
    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {

    }

    @Override
    protected VampirismPlayer copyFromPlayer(EntityPlayer old) {
        return null;
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
