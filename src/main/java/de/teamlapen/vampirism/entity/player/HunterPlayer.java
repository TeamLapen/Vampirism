package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IHunterPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Main class for hunter players
 */
public class HunterPlayer implements IHunterPlayer,ISyncable.ISyncableExtendedProperties {

    private final EntityPlayer player;

    public HunterPlayer(EntityPlayer player) {
        this.player=player;
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
    public void sync(boolean all) {

    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {

    }
}
