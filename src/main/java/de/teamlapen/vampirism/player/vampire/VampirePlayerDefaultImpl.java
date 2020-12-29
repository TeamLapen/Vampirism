package de.teamlapen.vampirism.player.vampire;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;


/**
 * Default Implementation of {@link IVampirePlayer} for the entity capability which is never used, since a default implementation, does not accept constructor parameter.
 */
@SuppressWarnings("ConstantConditions")
@Deprecated
class VampirePlayerDefaultImpl implements IVampirePlayer {

    private final static Logger LOGGER = LogManager.getLogger(VampirePlayerDefaultImpl.class);

    public VampirePlayerDefaultImpl() {
        LOGGER.error("Created Default Implementation. THIS SHOULD NOT BE DONE. The default impl does absolutely nothing");
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
    public BITE_TYPE determineBiteType(LivingEntity entity) {
        return null;
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {

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
    public IBloodStats getBloodStats() {
        return null;
    }

    @Override
    public IFaction getDisguisedAs() {
        return null;
    }


    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }


    @Nonnull
    @Override
    public ITaskManager getTaskManager() {
        return null;
    }

    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        return null;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return null;
    }

    @Override
    public PlayerEntity getRepresentingPlayer() {
        return null;
    }

    @Override
    public ISkillHandler<IVampirePlayer> getSkillHandler() {
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
    public EnumStrength isGettingGarlicDamage(IWorld iWorld) {
        return EnumStrength.NONE;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forcerefresh) {
        return EnumStrength.NONE;
    }

    @Override
    public boolean isGettingSundamage(IWorld world, boolean forcerefresh) {
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
    public int onBite(IVampire biter) {
        return 0;
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {

    }

    @Override
    public void unUnlockVision(@Nonnull IVampireVision vision) {

    }

    @Override
    public void unlockVision(@Nonnull IVampireVision vision) {

    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        return false;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }
}
