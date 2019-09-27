package de.teamlapen.vampirism.entity.minions.vampire;

import de.teamlapen.vampirism.api.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.minions.IMinionLordWithSaveable;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMinion;
import de.teamlapen.vampirism.entity.goals.FleeGarlicVampireGoal;
import de.teamlapen.vampirism.entity.minions.ai.FollowLordMinionGoal;
import de.teamlapen.vampirism.entity.minions.commands.DefendLordCommand;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Vampire minion that can be saved with the lord
 */
public class VampireMinionSaveableEntity extends VampireMinionBaseEntity implements IVampireMinion.Saveable {

    private final static Logger LOGGER = LogManager.getLogger(VampireMinionSaveableEntity.class);
    private final ArrayList<IMinionCommand> commands = new ArrayList<>();
    protected IMinionLordWithSaveable lord;

    public VampireMinionSaveableEntity(EntityType<? extends VampireMinionSaveableEntity> type, World world) {
        super(type, world);
        commands.add(getActiveCommand());
//        commands.add(new AttackHostileExceptPlayer(1, this));
//        commands.add(new AttackHostileIncludingPlayer(2, this));
//        commands.add(new JustFollowCommand(3));
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float value) {
        if (DamageSource.IN_WALL.equals(damageSource)) {
            return false;
        } else {
            return super.attackEntityFrom(damageSource, value);
        }
    }

    @Override
    public ArrayList<IMinionCommand> getAvailableCommands(IMinionLord lord) {
        return commands;
    }
//
//    /**
//     * Converts this minion to a remote minion
//     */
//    public void convertToRemote() {
//        EntityRemoteVampireMinion remote = (EntityRemoteVampireMinion) EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_MINION_REMOTE_NAME, world);
//        remote.copyDataFromMinion(this);
//        remote.setHealth(this.getHealth());
//        remote.copyLocationAndAnglesFrom(this);
//        IMinionLord lord = getLord();
//        if (lord != null) {
//            if (lord instanceof VampirePlayer) {
//                lord.getMinionHandler().unregisterMinion(this);
//                remote.setLord(lord);
//            } else {
//                Logger.w(TAG, "The converted minion %s cannot be controlled by this lord %s", remote, lord);
//            }
//
//        }
//        world.spawnEntityInWorld(remote);
//        this.remove();
//    }

    @Override
    public List<IMinionCommand> getAvailableRemoteCommands(IMinionLord lord) {
        return null;
    }

    @Override
    public IMinionCommand getCommand(int id) {
        if (id < commands.size())
            return commands.get(id);
        return null;
    }

    @Override
    public
    @Nullable
    IMinionLord getLord() {
        return lord;
    }

    @Override
    public boolean preventDespawn() {
        return lord != null;
    }

    @Override
    public void livingTick() {
        if (!this.world.isRemote) {
            if (lord == null) {

            } else if (!lord.isTheEntityAlive()) {
                lord = null;
                this.attackEntityFrom(DamageSource.MAGIC, 1000);
            }

        }
        super.livingTick();
    }

    /**
     * Makes sure minions which are saved with their lord do not interact with portals
     */
    @Override
    public void setPortal(BlockPos blockPos) {

    }

    @Override
    protected
    @Nonnull
    IMinionCommand createDefaultCommand() {
        return new DefendLordCommand(0, this, 1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(7, new FollowLordMinionGoal(this, 1.0D));
        this.goalSelector.addGoal(14, new FleeSunGoal(this, 0.9F));
        this.goalSelector.addGoal(14, new FleeGarlicVampireGoal(this, 0.9F, false));
    }

    @Override
    public void setLord(IMinionLord lord) {
        if (lord instanceof IMinionLordWithSaveable) {
            if (!lord.equals(this.lord)) {
                ((IMinionLordWithSaveable) lord).getSaveableMinionHandler().registerMinion(this, true);
                this.lord = (IMinionLordWithSaveable) lord;
            }
        } else {
            LOGGER.warn("Cannot set lord {} since it is not a IMinionLordWithSaveable", lord);
        }

    }
}
