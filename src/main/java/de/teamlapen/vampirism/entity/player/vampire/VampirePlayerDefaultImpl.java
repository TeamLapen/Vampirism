package de.teamlapen.vampirism.entity.player.vampire;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
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


/**
 * Default Implementation of {@link IVampirePlayer} for the entity capability which is never used, since a default implementation, does not accept constructor parameter.
 */
public class VampirePlayerDefaultImpl implements IVampirePlayer {


    public VampirePlayerDefaultImpl() {
        VampirismMod.log.e("VampirePlayerCapability", "Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");
    }

    @Override
    public void activateVision(@Nullable IVampireVision vision) {

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
    public void consumeBlood(int amt, float saturationMod) {

    }

    @Override
    public BITE_TYPE determineBiteType(EntityLivingBase entity) {
        return null;
    }

    @Override
    public boolean doesResistGarlic(EnumGarlicStrength strength) {
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
    public float getBloodSaturation() {
        return 0;
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
    public int getMaxMinionCount() {
        return 0;
    }

    @Override
    public EntityLivingBase getMinionTarget() {
        return null;
    }

    @Override
    public Predicate<? super Entity> getNonFriendlySelector(boolean otherFactionPlayers) {
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

    @Override
    public EnumGarlicStrength isGettingGarlicDamage() {
        return null;
    }

    @Override
    public EnumGarlicStrength isGettingGarlicDamage(boolean forcerefresh) {
        return null;
    }

    @Override
    public boolean isGettingSundamage(boolean forcerefresh) {
        return false;
    }

    @Override
    public boolean isGettingSundamage() {
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
    public EntityPlayer.EnumStatus trySleep(BlockPos pos) {
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
