package de.teamlapen.vampirism.entity.player.hunter;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.player.PlayerModifiers;
import de.teamlapen.vampirism.entity.player.VampirismPlayer;
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
    public String getPropertyKey() {
        return VampirismAPI.HUNTER_FACTION.prop;
    }

    @Override
    public void saveData(NBTTagCompound compound) {

    }

    @Override
    public void loadData(NBTTagCompound compound) {

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
    protected void onLevelChanged() {
        PlayerModifiers.applyModifier(player, SharedMonsterAttributes.attackDamage, "Hunter", getLevel(), Balance.hp.STRENGTH_LCAP, Balance.hp.STRENGTH_MAX_MOD, Balance.hp.STRENGTH_TYPE);
    }

    @Override
    protected void loadUpdate(NBTTagCompound nbt) {

    }

    @Override
    protected void writeFullUpdate(NBTTagCompound nbt) {
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

    @Override
    public Predicate<? super Entity> getNonFriendlySelector(boolean otherFactionPlayers) {
        return Predicates.alwaysTrue();//TODO adjust
    }
}
