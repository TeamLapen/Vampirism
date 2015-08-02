package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.entity.ai.EntityAIStayHere;
import de.teamlapen.vampirism.entity.ai.EntityAIWarnPlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Entity to guard the castle portal. Attacks all players of non max rang
 */
public class EntityPortalGuard extends EntityMob {
	public EntityPortalGuard(World world) {
		super(world);
		IEntitySelector playerSelector=new IEntitySelector() {
			@Override public boolean isEntityApplicable(Entity entity) {
				if(entity instanceof EntityPlayer){
					return VampirePlayer.get((EntityPlayer) entity).getLevel()< REFERENCE.HIGHEST_REACHABLE_LEVEL;
				}
				return false;
			}
		};
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		this.tasks.addTask(2,new EntityAIWatchClosest(this,EntityPlayer.class,8.0F));
		this.tasks.addTask(2,new EntityAIWarnPlayer(this,5,"text.vampirism.guard_stay_away_level_low",60,playerSelector));
		this.targetTasks.addTask(1,new EntityAINearestAttackableTarget(this, EntityPlayer.class, 1, true, true, playerSelector));
		this.setSize(0.9F, 1.8F);
		this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
		this.entityCollisionReduction=1.0F;

	}

	@Override protected boolean isAIEnabled() {
		return true;
	}

	@Override protected boolean canDespawn() {
		return false;
	}

	@Override protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(100.0D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100000.0D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1D);
	}

	@Override public boolean canBePushed() {
		return false;
	}

	@Override public void addVelocity(double p_70024_1_, double p_70024_3_, double p_70024_5_) {

	}
}
