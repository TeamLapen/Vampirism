package de.teamlapen.vampirism.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import de.teamlapen.vampirism.util.MobProperties;

public abstract class MobVampirism extends EntityMob{
	private final float WATCH_DISTANCE=6.0F;
	private final float WANDER_SPEED=0.25F;
    

	public MobVampirism(World par1World) {
            super(par1World);
            

            this.getNavigator().setAvoidsWater(true);
            this.setSize(0.6F, 1.8F);
            
            this.isImmuneToFire = false;

            //Now, we have the AI. Each number in the addTask is a priority. 0 is the highest, the largest is lowest.
            //They should be set in the order which the mob should focus, because it can only do one thing at a time. I'll explain my choice for order below.
            //There are tonnes of tasks you can add. Look in the JavaDocs or other mob classes to find some more!

            //Swimming should ALWAYS be first. Otherwise if your mob falls in water, but it's running away from you or something it'll drown.
            this.tasks.addTask(0, new EntityAISwimming(this));


            //This makes the mob walk around. Without it, it'd just stand still.
            this.tasks.addTask(100, new EntityAIWander(this, WANDER_SPEED));

            //This makes the mob watch the nearest player, within a range set by the float.
            this.tasks.addTask(101, new EntityAIWatchClosest(this, EntityPlayer.class, WATCH_DISTANCE));

            //Finally, this makes it look around when it's not looking at a player or wandering.
            this.tasks.addTask(101, new EntityAILookIdle(this));
    }

    //This is required. If it's false, none of the above takes effect.
    public boolean isAIEnabled() {
            return true;
    }
}
