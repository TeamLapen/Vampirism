package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;


/**
 * Default Implementation of {@link IVampirePlayer} for the entity capability which is never used, since a default implementation, does not accept constructor parameter.
 */
@Deprecated
class VampirePlayerDefaultImpl implements IVampirePlayer {


    public VampirePlayerDefaultImpl() {
        VampirismMod.log.e("VampirePlayerCapability", "Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");
    }

    @Override
    public void activateVision(@Nullable IVampireVision vision) {

    }

    @Override
    public float calculateFireDamage(float amount) {
        return amount;
    }

    @Override
    public boolean canBeBitten(IVampire biter) {
        return false;
    }

    @Override
    public boolean canLeaveFaction() {
        return false;
    }

    @Override
    public BITE_TYPE determineBiteType(EntityLivingBase entity) {
        return null;
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod) {

    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    public IActionHandler<IVampirePlayer> getActionHandler() {
        return null;
    }

    @Nullable
    @Override
    public IVampireVision getActiveVision() {
        return null;
    }

    @Override
    public int getBloodLevel() {
        return 0;
    }

    @Override
    public IBloodStats getBloodStats() {
        return null;
    }

    @Override
    public float getBloodSaturation() {
        return 0;
    }

    @Override
    public IFaction getDisguisedAs() {
        return null;
    }

    @Override
    public IPlayableFaction<IVampirePlayer> getFaction() {
        return null;
    }

    @Override
    public long getLastComebackCall() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getMaxLevel() {
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
    public Predicate<Entity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        return null;
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return null;
    }

    @Override
    public EntityPlayer getRepresentingPlayer() {
        return null;
    }

    @Override
    public ISkillHandler<IVampirePlayer> getSkillHandler() {
        return null;
    }

    @Override
    public double getTheDistanceSquared(Entity e) {
        return 0;
    }

    @Override
    public UUID getThePersistentID() {
        return null;
    }

    @Override
    public int getTicksInSun() {
        return 0;
    }

    @Override
    public boolean isAutoFillEnabled() {
        return false;
    }

    @Override
    public boolean isDisguised() {
        return false;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage() {
        return EnumStrength.NONE;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(boolean forcerefresh) {
        return EnumStrength.NONE;
    }

    @Override
    public boolean isGettingSundamage(boolean forcerefresh) {
        return false;
    }


    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean isTheEntityAlive() {
        return false;
    }

    @Override
    public boolean isVampireLord() {
        return false;
    }

    @Override
    public int onBite(IVampire biter) {
        return 0;
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {

    }

    @Override
    public EntityPlayer.SleepResult trySleep(BlockPos pos) {
        return null;
    }

    @Override
    public void unUnlockVision(@Nonnull IVampireVision vision) {

    }

    @Override
    public void unlockVision(@Nonnull IVampireVision vision) {

    }

    @Override
    public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {

    }
}
