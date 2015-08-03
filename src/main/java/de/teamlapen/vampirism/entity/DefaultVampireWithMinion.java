package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.entity.minions.IMinionLord;
import de.teamlapen.vampirism.entity.minions.SaveableMinionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Default vampire with minions
 */
public abstract class DefaultVampireWithMinion extends DefaultVampire implements IMinionLord {

    private final SaveableMinionHandler minionHandler;

    public DefaultVampireWithMinion(World world) {
        super(world);
        minionHandler = new SaveableMinionHandler(this);
    }

    @Override
    public long getLastComebackCall() {
        return 0;
    }


    @Override
    public SaveableMinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public EntityLivingBase getMinionTarget() {
        return this.getAttackTarget();
    }

    @Override
    public EntityLivingBase getRepresentingEntity() {
        return this;
    }

    @Override
    public double getTheDistanceSquared(Entity e) {
        return this.getDistanceSqToEntity(e);
    }

    @Override
    public UUID getThePersistentID() {
        return this.getPersistentID();
    }

    @Override
    public boolean isTheEntityAlive() {
        return this.isEntityAlive();
    }

    @Override
    public void onLivingUpdate() {
        this.minionHandler.checkMinions();
        this.minionHandler.addLoadedMinions();
        super.onLivingUpdate();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        minionHandler.loadMinions(nbt.getTagList("minions", 10));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setTag("minions", minionHandler.getMinionsToSave());
    }
}
