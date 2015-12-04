package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.network.ISyncable;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.FractionRegistry;
import de.teamlapen.vampirism.api.entity.player.IFractionPlayer;
import de.teamlapen.vampirism.api.entity.player.IPlayerEventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Basic class for all of Vampirism's players.
 * Implements basic methods for level or minion handling
 */
public abstract class VampirismPlayer implements IFractionPlayer, ISyncable.ISyncableExtendedProperties, IPlayerEventListener,IMinionLord {

    protected final EntityPlayer player;

    private final String TAG_LEVEL="level";

    private int level;

    public VampirismPlayer(EntityPlayer player){
        this.player=player;
    }
    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void levelUp() {
        setLevel(getLevel()+1);
    }

    @Override
    public EntityPlayer getRepresentingPlayer() {
        return player;
    }

    @Override
    public void setLevel(int level) {
        if(level>=0&&level<=getMaxLevel()){
            if(level>0){
                IFractionPlayer active=FractionRegistry.getActiveFraction(player);
                if(active!=null&&active!=this){
                    player.addChatMessage(new ChatComponentTranslation("text.vampirism.player.multiple_factions"));
                    return;
                }
            }
            this.level=level;
            onLevelChanged();
        }
    }

    /**
     * Called when the level is changed
     */
    protected  void onLevelChanged(){
        this.sync(true);
    }

    /**
     * Max level this player type can reach
     * @return
     */
    protected abstract int getMaxLevel();

    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }

    @Override
    public void sync(boolean all) {
        //TODO
    }

    @Override
    public long getLastComebackCall() {
        return 0;
    }

    @Override
    public int getMaxMinionCount() {
        return 0;
    }

    @Override
    public EntityLivingBase getMinionTarget() {
        return null;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return player;
    }

    @Override
    public double getTheDistanceSquared(Entity e) {
        if(e==null)return Double.MAX_VALUE;
        return player.getDistanceSqToEntity(e);
    }

    @Override
    public UUID getThePersistentID() {
        return player.getUniqueID();
    }

    @Override
    public boolean isTheEntityAlive() {
        return player.isEntityAlive();
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        nbt.setInteger(TAG_LEVEL,level);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        level=nbt.getInteger(TAG_LEVEL);
    }

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public void loadUpdateFromNBT(NBTTagCompound nbt) {
        if(nbt.hasKey(TAG_LEVEL)){
            level=nbt.getInteger(TAG_LEVEL);
        }
    }

    @Override
    public void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setInteger(TAG_LEVEL,level);
    }


    @Override
    public void copyFrom(EntityPlayer old) {
        VampirismPlayer p=copyFromPlayer(old);
        this.level=p.getLevel();
    }

    /**
     * Copy all relevant values from the given player and return itself, so {@link VampirismPlayer} can copy it's values as well
     * @param old
     * @return
     */
    protected abstract VampirismPlayer copyFromPlayer(EntityPlayer old);
}
