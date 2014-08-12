package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.MobProperties;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

//Your declaration. If your mob swims, change EntityAnimal to EntityWaterMob.
public class EntityVampire extends EntityMob {
      

	public EntityVampire(World par1World) {
              super(par1World);
              
              this.applyEntityAttributes();
              this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MobProperties.vampire_maxHealth);
              
              //TODO Texture
              //this.texture = "/co/uk/silvania/Remula/resources/mobglog.png";
              //The below means if possible, it wont walk into water
              this.getNavigator().setAvoidsWater(true);
              //This is the hitbox size. I believe it starts in the center and grows outwards
              this.setSize(1.5F, 0.9F);
              //Pretty self-explanatory. 
              this.isImmuneToFire = false;
              float var2 = 0.25F;

              //Now, we have the AI. Each number in the addTask is a priority. 0 is the highest, the largest is lowest.
              //They should be set in the order which the mob should focus, because it can only do one thing at a time. I'll explain my choice for order below.
              //There are tonnes of tasks you can add. Look in the JavaDocs or other mob classes to find some more!

              //Swimming should ALWAYS be first. Otherwise if your mob falls in water, but it's running away from you or something it'll drown.
              this.tasks.addTask(0, new EntityAISwimming(this));

              //This makes the mob run away when you punch it
              this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));

              //This makes the mob walk around. Without it, it'd just stand still.
              this.tasks.addTask(2, new EntityAIWander(this, var2));

              //This makes the mob watch the nearest player, within a range set by the float.
              this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));

              //Finally, this makes it look around when it's not looking at a player or wandering.
              this.tasks.addTask(4, new EntityAILookIdle(this));
      }

      //This is required. If it's false, none of the above takes effect.
      public boolean isAIEnabled() {
              return true;
      }
      
      //TODO Sounds
      protected String getLivingSound() {
              return "mob.glog.say";
      }
      
      protected String getHurtSound() {
              return "mob.glog.say";
      }
      
      protected String getDeathSound() {
              return "mob.glog.death";
      }

      
      //This is required regardless of if your animal can breed or not. Set to null if it can't breed - I wont cover breeding here.
      public EntityAgeable createChild(EntityAgeable var1) {
              return null;
      }
}
