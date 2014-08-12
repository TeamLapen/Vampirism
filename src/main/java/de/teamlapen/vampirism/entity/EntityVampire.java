package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.MobProperties;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

//Your declaration. If your mob swims, change EntityAnimal to EntityWaterMob.
public class EntityVampire extends BasicMob {
      

	public EntityVampire(World par1World) {
              super(par1World);
              
              super.applyEntityAttributes();
              this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MobProperties.vampire_maxHealth);
              this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(MobProperties.vampire_attackDamage);
              this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MobProperties.vampire_movementSpeed);
              
              //TODO Texture
              //this.texture = "/co/uk/silvania/Remula/resources/mobglog.png";
              
              //Avoids Vampire Hunters TODO Distance (3rd argument)
              this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityVampireHunter.class, 10.0F, MobProperties.vampire_movementSpeed, MobProperties.vampire_movementSpeed*1.5));
              //Attacks Player
              this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, MobProperties.vampire_movementSpeed*2, true));
              //Avoids sun
              this.tasks.addTask(10, new EntityAIFleeSun(this, MobProperties.vampire_movementSpeed*1.5));
              
	}
      
      //TODO Sounds
      @Override
	protected String getLivingSound() {
              return "mob.glog.say";
      }
      
      @Override
	protected String getHurtSound() {
              return "mob.glog.say";
      }
      
      @Override
	protected String getDeathSound() {
              return "mob.glog.death";
      }
}
