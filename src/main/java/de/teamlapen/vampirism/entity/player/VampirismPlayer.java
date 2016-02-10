package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.FactionRegistry;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
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
public abstract class VampirismPlayer implements IFactionPlayer, ISyncable.ISyncableExtendedProperties, IPlayerEventListener, IMinionLord {


    private static final String TAG = "VampirismPlayer";
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
    public void setLevel(int level) {
        //VampirismMod.log.t("Setting level %s (c %s,max %s)", level, getLevel(), getMaxLevel());
        if (level >= 0 && level <= getMaxLevel() && getLevel() != level) {
            if(level>0){
                IFactionPlayer active = FactionRegistry.getActiveFactionPlayer(player);
                if(active!=null&&active!=this){
                    //Should be detected before setLevel is even called
                    player.addChatMessage(new ChatComponentTranslation("text.vampirism.player.multiple_factions"));
                    return;
                }
            }
            this.level=level;
            onLevelChanged();
            this.sync(true);
        }
    }

    @Override
    public void levelUp() {
        setLevel(getLevel() + 1);
    }

    @Override
    public EntityPlayer getRepresentingPlayer() {
        return player;
    }

    /**
     * Called when the level is changed
     * Can be overridden in subclasses
     */
    protected void onLevelChanged() {
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
    public final void saveNBTData(NBTTagCompound nbt) {
        NBTTagCompound properties = new NBTTagCompound();
        properties.setInteger(TAG_LEVEL, level);
        saveData(properties);
        nbt.setTag(getPropertyKey(), properties);
    }

    protected abstract void saveData(NBTTagCompound nbt);

    @Override
    public final void loadNBTData(NBTTagCompound nbt) {
        NBTTagCompound properties = nbt.getCompoundTag(getPropertyKey());
        if (properties == null) {
            VampirismMod.log.i(TAG, "VampirePlayer data for %s cannot be loaded. It probably does not exist", player);
            return;
        }
        level = properties.getInteger(TAG_LEVEL);
        loadData(properties);
    }

    protected abstract void loadData(NBTTagCompound nbt);

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public final void loadUpdateFromNBT(NBTTagCompound nbt) {
        if(nbt.hasKey(TAG_LEVEL)){
            level=nbt.getInteger(TAG_LEVEL);

        }
        loadData(nbt);
    }

    /**
     * Can be overridden to load data from updates in subclasses
     *
     * @param nbt
     */
    protected void loadUpdate(NBTTagCompound nbt) {
    }

    @Override
    public final void writeFullUpdateToNBT(NBTTagCompound nbt) {
        nbt.setInteger(TAG_LEVEL,level);
        writeFullUpdate(nbt);
    }

    /**
     * Can be overridden to put data into updates in subclasses
     *
     * @param nbt
     */
    protected void writeFullUpdate(NBTTagCompound nbt){}

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

    @Override
    public void sync(boolean all) {
        HelperLib.sync(this, player, all);
    }
}
