package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IVampirePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Main class for Vampire Players.
 */
public class VampirePlayer implements IVampirePlayer,ISyncable.ISyncableExtendedProperties {

    private final EntityPlayer player;

    public VampirePlayer(EntityPlayer player) {
        this.player=player;
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
    public void loadUpdateFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {

    }

    @Override
    public int getBloodLevel() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public EntityPlayer getRepresentingPlayer() {
        return player;
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
    public void setLevel(int level) {

    }

    @Override
    public void sync(boolean all) {

    }

    @Override
    public boolean canTurnOthers() {
        return false;
    }

    @Override
    public boolean isGettingSundamage(boolean forcerefresh) {
        return false;
    }

    @Override
    public boolean isGettingSundamge() {
        return false;
    }

    /**
     * Copy the vampire player fields from the given player
     * @param player
     */
    public void copyFrom(EntityPlayer player){

    }
}
