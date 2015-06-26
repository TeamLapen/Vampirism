package de.teamlapen.vampirism.entity.ai;

import java.util.List;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.DefaultVampire;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.minions.EntityRemoteVampireMinion;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Vampire bites nearby biteable entity
 * @author Maxanier
 *
 */
public class EntityAIBiteNearbyEntity extends EntityAIBase {

	protected final DefaultVampire vampire;
	protected VampireMob mob;
	
	public EntityAIBiteNearbyEntity(DefaultVampire vampire){
		this.vampire=vampire;
	}
	
	@Override
	public boolean shouldExecute() {
		if(vampire.getRNG().nextInt(10)==0){
			List list=vampire.worldObj.getEntitiesWithinAABB(EntityCreature.class, vampire.boundingBox.expand(2, 2, 2));
			for(Object o:list){
				mob=VampireMob.get((EntityCreature) o);
				if(mob.canBeBitten())return true;
			}
		}
		mob=null;
		return false;
	}
	
	@Override
	public void startExecuting(){
		int amount=mob.bite();
		vampire.worldObj.playSoundAtEntity(vampire, REFERENCE.MODID+":player.bite", 1.0F, 1.0F);
		addBlood(amount);
	}
	
	protected void addBlood(int amount){
		
	}
	
	/**
	 * Bites nearbe entity and fills the equipped bottle
	 * @author Max
	 *
	 */
	public static class EntityAIMinionCollectFromNearby extends EntityAIBiteNearbyEntity{

		public EntityAIMinionCollectFromNearby(EntityRemoteVampireMinion vampire) {
			super(vampire);
		}
		
		@Override
		protected void addBlood(int amount){
			ItemStack item=vampire.getEquipmentInSlot(0);
			if(item!=null){
				if(item.getItem().equals(Items.glass_bottle)){
					ItemStack stack1=new ItemStack(ModItems.bloodBottle,1,0);
					ItemBloodBottle.addBlood(stack1, amount);
					vampire.setCurrentItemOrArmor(0, stack1);
					return;
				}
				if(item.getItem().equals(ModItems.bloodBottle)){
					ItemBloodBottle.addBlood(item, amount);
					return;
				}
			}
		}
		
	}

}
