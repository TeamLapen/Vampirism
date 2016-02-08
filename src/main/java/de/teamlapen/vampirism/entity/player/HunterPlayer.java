package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IHunterPlayer;
import de.teamlapen.vampirism.config.BalanceHunterPlayer;
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

    static {
        VampirismAPI.registerPlayerEventReceivingProperty(VampirismAPI.HUNTER_FACTION.prop);
    }

    public HunterPlayer(EntityPlayer player) {
        super(player);
    }

    /**
     * Don't call before the construction event of the player entity is finished
     * @param player
     * @return
     */
    public static HunterPlayer get(EntityPlayer player){
        return (HunterPlayer) VampirismAPI.HUNTER_FACTION.getProp(player);
    }
    public static void register(EntityPlayer player){
        player.registerExtendedProperties(VampirismAPI.HUNTER_FACTION.prop, new HunterPlayer(player));
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
    public PlayableFaction<IHunterPlayer> getFaction() {
        return VampirismAPI.HUNTER_FACTION;
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
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.attackDamage, "Hunter", getLevel(), BalanceHunterPlayer.STRENGTH_LCAP, BalanceHunterPlayer.STRENGTH_MAX_MOD, BalanceHunterPlayer.STRENGTH_TYPE);
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

    @Override
    public void onPlayerClone(EntityPlayer original) {
        copyFrom(original);
    }
}
