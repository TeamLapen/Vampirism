package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.eclipse.jdt.annotation.NonNull;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.ai.EntityAIBiteNearbyEntity;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveAround;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveToBiteable;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveToLord;
import de.teamlapen.vampirism.entity.ai.EntityAIWaitForBottle;
import de.teamlapen.vampirism.item.ItemBloodBottle;

/**
 * Makes the minion collect blood and therefore picks up bottles
 * @author Maxanier
 *
 */
public class CollectBloodCommand extends DefaultMinionCommand {

	protected final EntityRemoteVampireMinion minion;
	protected final EntityAIBase runAround;
	protected final EntityAIBase runToPlayer;
	protected final EntityAIBase bite;
	protected final EntityAIBase moveToBiteable;
	protected final EntityAIBase waitForBottle;
	
	public CollectBloodCommand(int id,EntityRemoteVampireMinion m) {
		super(id);
		minion=m;
		runAround=new EntityAIMoveAround(m.getRepresentingEntity(),1.0,false);
		runToPlayer=new EntityAIMoveToLord.EntityAIMinionBringBottle(m);
		bite=new EntityAIBiteNearbyEntity.EntityAIMinionCollectFromNearby(m);
		moveToBiteable=new EntityAIMoveToBiteable(m);
		waitForBottle=new EntityAIWaitForBottle(m);
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.collectblood";
	}

	@Override
	public void onActivated() {
		minion.tasks.addTask(9, runToPlayer);
		minion.tasks.addTask(10, waitForBottle);
		minion.tasks.addTask(11, bite);
		minion.tasks.addTask(11, moveToBiteable);
		minion.tasks.addTask(12, runAround);

	}

	@Override
	public void onDeactivated() {
		ItemStack item=minion.getRepresentingEntity().getEquipmentInSlot(0);
		if(item!=null&&(item.getItem().equals(ModItems.bloodBottle)||item.getItem().equals(Items.glass_bottle))){
			minion.getRepresentingEntity().entityDropItem(item, 0.1F);
			minion.getRepresentingEntity().setCurrentItemOrArmor(0, null);
		}
		minion.tasks.removeTask(runToPlayer);
		minion.tasks.removeTask(bite);
		minion.tasks.removeTask(runAround);
		minion.tasks.removeTask(runToPlayer);
	}

	@Override
	public int getMinU() {
		return 48;
	}

	@Override
	public int getMinV() {
		return 0;
	}
	
	@Override
	public boolean shouldPickupItem(@NonNull ItemStack item){
		
		if(item.getItem().equals(ModItems.bloodBottle)||item.getItem().equals(Items.glass_bottle)){
			ItemStack old=minion.getRepresentingEntity().getEquipmentInSlot(0);
			if(old==null)return true;
			if(ItemBloodBottle.getBlood(item)<ItemBloodBottle.getBlood(old)){
				return true;
			}
		}
		return false;
	}

}
