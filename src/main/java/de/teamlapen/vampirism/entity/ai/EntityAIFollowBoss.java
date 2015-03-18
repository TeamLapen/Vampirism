package de.teamlapen.vampirism.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowBoss extends EntityAIBase
{
    /** The child that is following its parent. */
    EntityLiving entity;
    EntityLiving boss;
    double speed;
    private int timer;

    public EntityAIFollowBoss(EntityLiving entity, double speed)
    {
    	if(!(entity instanceof IMinion)){
    		throw new IllegalArgumentException("This task can only be used by entitys which implement IMinion");
    	}
        this.entity=entity;
        this.speed=speed;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	boss=((IMinion)entity).getBoss();
    	if(boss==null){
    		return false;
    	}
    	return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (!this.boss.isEntityAlive())
        {
        	boss=null;
            return false;
        }
        else
        {
            double d0 = this.entity.getDistanceSqToEntity(this.boss);
            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.timer = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.boss = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (--this.timer <= 0)
        {
            this.timer = 10;
            this.entity.getNavigator().tryMoveToEntityLiving(this.boss, this.speed);
        }
    }
}
